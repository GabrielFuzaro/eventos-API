package com.eventos.eventos_api.model.input;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoIdInput {
    
    @NotNull
    private Long id;
}
