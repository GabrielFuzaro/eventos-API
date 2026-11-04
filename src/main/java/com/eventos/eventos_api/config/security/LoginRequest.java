package com.eventos.eventos_api.config.security;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Informe o usuário")
    @Size(max = 50, message = "Usuário deve ter no máximo 50 caracteres")
    private String username;

    @NotBlank(message = "Informe a senha")
    @Size(max = 100, message = "Senha deve ter no máximo 100 caracteres")
    private String password;
}
