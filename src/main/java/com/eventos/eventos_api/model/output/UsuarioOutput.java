package com.eventos.eventos_api.model.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioOutput { //Dados públicos do usuário: nunca inclua a senha aqui

    private String username;
    private String role;
}
