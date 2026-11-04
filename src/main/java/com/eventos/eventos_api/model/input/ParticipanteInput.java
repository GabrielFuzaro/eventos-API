package com.eventos.eventos_api.model.input;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter //Gera os gets
@Setter //Gera os sets
public class ParticipanteInput { //Declara o formato do input de participante (também usado na lista de espera)

    @Valid
    @NotNull(message = "Informe o evento")
    private EventoIdInput eventoId;

    @NotBlank(message = "Informe o nome")
    @Size(max = 30, message = "Nome deve ter no máximo 30 caracteres")
    private String nome;

    @NotBlank(message = "Informe o email")
    @Email(message = "Email inválido")
    @Size(max = 40, message = "Email deve ter no máximo 40 caracteres")
    private String email;
}
