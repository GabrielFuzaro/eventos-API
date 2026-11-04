package com.eventos.eventos_api.model.output;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListaEsperaOutput { //Formato de saída de uma inscrição na lista de espera

    private Long id;
    private Long eventoId;
    private String nome;
    private String email;
    private OffsetDateTime dataInscricao;
    private long posicao; //Posição na fila (1 = próximo a ser chamado)
}
