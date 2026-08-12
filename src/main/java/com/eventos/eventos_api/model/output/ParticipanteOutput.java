package com.eventos.eventos_api.model.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipanteOutput {
    
    private Long id;
    private EventoOutput evento;
    private String nome;
    private String email;
}
