package com.eventos.eventos_api.model.input;

import java.time.OffsetDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter //Gera os gets
@Setter //Gera os sets
public class EventoInput { //declara o formato do input para o evento
    
    @NotBlank(message = "Informe o nome do evento")
    @Size(max = 30, message = "Nome deve ter no máximo 30 caracteres")
    private String nome;

    @NotBlank(message = "Informe o local")
    @Size(max = 30, message = "Local deve ter no máximo 30 caracteres")
    private String local;

    @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 vaga")
    private int capacidade_maxima;

    @NotNull(message = "Informe a data do evento")
    private OffsetDateTime data_evento;

    @NotNull(message = "Informe a categoria")
    private Long categoriaId;

}
