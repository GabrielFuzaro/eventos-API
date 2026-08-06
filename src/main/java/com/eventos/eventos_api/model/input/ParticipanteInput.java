package com.eventos.eventos_api.model.input;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipanteInput {
    
    @Valid
    private EventoIdInput eventoId;
    private String nome;
    private String email;
}
