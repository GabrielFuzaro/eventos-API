package com.eventos.eventos_api.model.output;

import lombok.Getter;
import lombok.Setter;

@Getter //Gera os sets
@Setter //Gera os sets
public class ParticipanteOutput {    //Declara o formato do corpo de saída de um Participante
    
    private Long id;
    private EventoOutput evento;
    private String nome;
    private String email;
}
