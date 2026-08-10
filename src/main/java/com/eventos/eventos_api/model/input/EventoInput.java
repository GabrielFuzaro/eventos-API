package com.eventos.eventos_api.model.input;

import java.time.OffsetDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoInput {
    
    @Valid
    private String nome;
    private String local;
    private int capacidade_maxima;
    private OffsetDateTime data_evento;

}
