package com.eventos.eventos_api.domain.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "lista_espera")
public class ListaEspera { //Inscrição na lista de espera de um evento lotado

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //Muitas inscrições de espera pertencem a um único evento
    @NotNull
    private Evento evento;

    @NotBlank
    @Size(max = 30)
    private String nome;

    @NotBlank
    @Size(max = 40)
    private String email;

    @NotNull
    @Column(name = "data_inscricao")
    private OffsetDateTime dataInscricao; //Momento em que a pessoa entrou na fila
}
