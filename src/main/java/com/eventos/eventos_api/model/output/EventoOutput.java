package com.eventos.eventos_api.model.output;

import java.time.OffsetDateTime;

import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.eventos.eventos_api.domain.model.StatusEvento;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoOutput {
    
    @Valid
    private Long id;
    private String nome;
    private OffsetDateTime data;
    private OffsetDateTime data_evento;
    private String local;
    private int capacidade_maxima;
    private StatusEvento status;
}
