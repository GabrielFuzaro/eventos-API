package com.eventos.eventos_api.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.domain.model.StatusEvento;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncerramentoAutomaticoService {

    private final EventoRepository eventoRepository;

    public EncerramentoAutomaticoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
        System.out.println("🔥 EncerramentoAutomaticoService FOI CRIADO!");
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void encerrarEventos() {

        System.out.println("VERIFICANDO EVENTOS...");

        List<Evento> eventos = eventoRepository.buscarEventosParaEncerrar(
            OffsetDateTime.now(),
            StatusEvento.ABERTO
        );

        System.out.println("EVENTOS ENCONTRADOS: " + eventos.size());

        eventos.forEach(Evento::encerrar);

        eventoRepository.saveAll(eventos);
    }
}
