package com.eventos.eventos_api.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true) //Gera equals e hasgcodes parenas para includeds
@Getter //Gera os gets
@Setter //Gera os sets
@Entity //Declara a classe como uma entidade do banco de dados
public class Participante {
    

    @Id //Diz que é o id
    @EqualsAndHashCode.Include //Incluí na geração de equals e hascodes
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Declara a geração da variavel sendo equivalente a geração do banco (Auto_Increment) nesse caso
    private Long id;

    @ManyToOne //Diz que muitos participantes podem pertencer a um único evento
    @NotNull // não deixa o valor ser nulo
    private Evento evento;

    @NotBlank //Não permite valor nulo e nem string vazia
    @Size(max = 30) // Limita o tamanho da variavel
    private String nome;

    @NotBlank
    @Size(max = 40)
    private String email;
}
