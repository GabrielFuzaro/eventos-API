package com.eventos.eventos_api.model.input;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter //Gera os gets
@Setter //Gera os sets
public class EventoInput { //declara o formato do input para o evento
    
    private String nome;
    private String local;
    private int capacidade_maxima;
    private OffsetDateTime data_evento;

}
