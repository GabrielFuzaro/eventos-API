package com.eventos.eventos_api.config.security;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CadastroRequest {

    @NotBlank(message = "Informe o usuário")
    @Size(min = 3, max = 50, message = "Usuário deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Usuário pode ter apenas letras, números, ponto, hífen e sublinhado")
    private String username;

    @NotBlank(message = "Informe a senha")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    private String senha;
}
