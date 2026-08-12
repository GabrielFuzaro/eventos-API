package com.eventos.eventos_api.model.output;

import java.time.OffsetDateTime;
import com.eventos.eventos_api.domain.model.StatusEvento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoOutput {
    
    private Long id;
    private String nome;
    private OffsetDateTime data;
    private OffsetDateTime data_evento;
    private String local;
    private int capacidade_maxima;
    private StatusEvento status;
}
