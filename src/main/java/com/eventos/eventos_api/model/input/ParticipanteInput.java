package com.eventos.eventos_api.model.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipanteInput {

    private EventoIdInput eventoId;
    private String nome;
    private String email;
}
