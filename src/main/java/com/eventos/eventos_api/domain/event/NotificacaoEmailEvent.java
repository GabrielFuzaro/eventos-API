package com.eventos.eventos_api.domain.event;

import java.time.OffsetDateTime;

import com.eventos.eventos_api.domain.model.Evento;

import lombok.Getter;

/**
 * Publicado pelos services quando uma inscrição nova acontece. Carrega só dados simples (nada de entidade JPA),
 * porque o email é enviado depois do commit, em outra thread, quando a sessão do Hibernate já foi fechada.
 */
@Getter
public class NotificacaoEmailEvent {

    private final TipoNotificacao tipo;
    private final String nome;
    private final String email;
    private final String eventoNome;
    private final OffsetDateTime eventoData;
    private final String eventoLocal;
    private final Long posicaoFila; //Só para ENTRADA_LISTA_ESPERA

    private NotificacaoEmailEvent(TipoNotificacao tipo, String nome, String email, Evento evento, Long posicaoFila) {
        this.tipo = tipo;
        this.nome = nome;
        this.email = email;
        this.eventoNome = evento.getNome();
        this.eventoData = evento.getData_evento();
        this.eventoLocal = evento.getLocal();
        this.posicaoFila = posicaoFila;
    }

    public static NotificacaoEmailEvent inscricaoConfirmada(String nome, String email, Evento evento) {
        return new NotificacaoEmailEvent(TipoNotificacao.INSCRICAO_CONFIRMADA, nome, email, evento, null);
    }

    public static NotificacaoEmailEvent entradaListaEspera(String nome, String email, Evento evento, long posicao) {
        return new NotificacaoEmailEvent(TipoNotificacao.ENTRADA_LISTA_ESPERA, nome, email, evento, posicao);
    }

    public static NotificacaoEmailEvent vagaLiberada(String nome, String email, Evento evento) {
        return new NotificacaoEmailEvent(TipoNotificacao.VAGA_LIBERADA, nome, email, evento, null);
    }
}
