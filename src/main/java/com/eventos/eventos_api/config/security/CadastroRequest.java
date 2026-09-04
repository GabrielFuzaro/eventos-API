package com.eventos.eventos_api.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CadastroRequest {
    
    private String username;
    private String senha;
}
