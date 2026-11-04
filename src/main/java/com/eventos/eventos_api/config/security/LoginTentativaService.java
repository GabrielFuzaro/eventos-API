package com.eventos.eventos_api.config.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.exception.MuitasTentativasException;

@Service
public class LoginTentativaService { //Proteção contra força bruta: bloqueia usuário+IP após várias senhas erradas

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration BLOQUEIO = Duration.ofMinutes(15);

    private static class Tentativas {
        int falhas;
        Instant ultimaFalha;
        Instant bloqueadoAte;
    }

    //Em memória: zera quando a API reinicia (suficiente para uma única instância)
    private final Map<String, Tentativas> tentativas = new ConcurrentHashMap<>();

    public void verificarBloqueio(String username, String ip) {
        Tentativas t = tentativas.get(chave(username, ip));
        if (t != null && t.bloqueadoAte != null) {
            if (Instant.now().isBefore(t.bloqueadoAte)) {
                long minutos = Math.max(1, Duration.between(Instant.now(), t.bloqueadoAte).toMinutes() + 1);
                throw new MuitasTentativasException(
                    "Muitas tentativas de login. Tente novamente em " + minutos + " minuto(s).");
            }
            tentativas.remove(chave(username, ip)); //Bloqueio expirou
        }
    }

    public void registrarFalha(String username, String ip) {
        tentativas.compute(chave(username, ip), (k, t) -> {
            Tentativas atual = t == null ? new Tentativas() : t;
            atual.falhas++;
            atual.ultimaFalha = Instant.now();
            if (atual.falhas >= MAX_TENTATIVAS) {
                atual.bloqueadoAte = Instant.now().plus(BLOQUEIO);
                atual.falhas = 0;
            }
            return atual;
        });
    }

    public void registrarSucesso(String username, String ip) {
        tentativas.remove(chave(username, ip));
    }

    //Remove registros antigos para o mapa não crescer sem limite (ex.: tentativas com muitos usuários diferentes).
    //Falhas sem bloqueio "esquecem" depois do mesmo tempo do bloqueio
    @Scheduled(fixedRate = 300000)
    public void limparRegistrosAntigos() {
        Instant agora = Instant.now();
        tentativas.entrySet().removeIf(e -> {
            Tentativas t = e.getValue();
            boolean bloqueioAtivo = t.bloqueadoAte != null && agora.isBefore(t.bloqueadoAte);
            boolean falhaRecente = t.ultimaFalha != null && agora.isBefore(t.ultimaFalha.plus(BLOQUEIO));
            return !bloqueioAtivo && !falhaRecente;
        });
    }

    private String chave(String username, String ip) {
        return (username == null ? "" : username.trim().toLowerCase(Locale.ROOT)) + "|" + ip;
    }
}
