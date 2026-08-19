package com.eventos.eventos_api.model.input;

import lombok.Getter;
import lombok.Setter;

@Getter //Gera os gets
@Setter //Gera os sets
public class ParticipanteInput { //Declara o formato do input de participante

    private EventoIdInput eventoId;
    private String nome;
    private String email;
}
