package com.eventos.eventos_api.model.output;

import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipanteOutput {
    
    @Valid
    private Long id;
    private EventoOutput evento;
    private String nome;
    private String email;
}
