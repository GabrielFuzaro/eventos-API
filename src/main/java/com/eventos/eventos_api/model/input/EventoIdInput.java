package com.eventos.eventos_api.model.input;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter //gera os gets
@Setter //gera os sets
public class EventoIdInput {   //Input de ID do evento
    
    @NotNull
    private Long id;
}
