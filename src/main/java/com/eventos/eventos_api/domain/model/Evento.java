package com.eventos.eventos_api.domain.model;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.eventos.eventos_api.domain.exception.NegocioExeption;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
public class Evento {
    
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String nome;

    //@NotNull
    private OffsetDateTime data;

    @NotNull
    private OffsetDateTime data_evento;

    @NotBlank
    @Size(max = 30)
    private String local;

    @NotNull
    private int capacidade_maxima;

    @Enumerated(EnumType.STRING)
    private StatusEvento status;


    public boolean podeSerEncerrado() {
    return StatusEvento.ABERTO.equals(getStatus());
}

public void encerrar() {

    if (!podeSerEncerrado()) {
        throw new NegocioExeption("Evento não pode ser encerrado!");
    }

    setStatus(StatusEvento.ENCERRADO);
}

}
