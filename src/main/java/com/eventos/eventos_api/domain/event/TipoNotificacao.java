package com.eventos.eventos_api.domain.event;

public enum TipoNotificacao {
    INSCRICAO_CONFIRMADA,  //Inscrição feita diretamente no evento
    ENTRADA_LISTA_ESPERA,  //Entrou na lista de espera de um evento lotado
    VAGA_LIBERADA          //Saiu da lista de espera e virou participante
}
