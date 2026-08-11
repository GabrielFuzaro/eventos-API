package com.eventos.eventos_api.model.input;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoInput {
    
    private String nome;
    private String local;
    private int capacidade_maxima;
    private OffsetDateTime data_evento;

}
