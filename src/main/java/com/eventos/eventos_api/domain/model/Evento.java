package com.eventos.eventos_api.domain.model;

import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.eventos.eventos_api.domain.exception.NegocioExeption;

import lombok.EqualsAndHashCode; 
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true) //gera equals and hashcode apenas para includeds
@Getter //Gera os gets
@Setter // Gera os sets
@Entity //Declara class como representação de uma entidade do banco de dados
public class Evento {
    
    @Id //Declara como id
    @EqualsAndHashCode.Include // inclui na geração de equals e hashcodes
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Gera o valor do mesmo jeito que o banco (Auto_Increment) nesse caso
    private Long id; 

    @NotBlank //Nao permite valor nulo e nem string vazia
    @Size(max = 30) //Limita o tamanho da string
    private String nome;

    //@NotNull
    private OffsetDateTime data;

    @NotNull // Valor nao pode ser nulo
    private OffsetDateTime data_evento;

    @NotBlank // Nao permite valor nulo ou string vazia
    @Size(max = 30)
    private String local;

    @NotNull
    private int capacidade_maxima;

    @Enumerated(EnumType.STRING) //Declara variável como um tipo Enum
    private StatusEvento status;


    public boolean podeSerEncerrado() {           //Método para verificar se um evento pode ser encerrado
        return StatusEvento.ABERTO.equals(getStatus()) || StatusEvento.LOTADO.equals(getStatus());
    }

    public void encerrar() {              //Método para encerrar evento

        if (!podeSerEncerrado()) {
            throw new NegocioExeption("Evento não pode ser encerrado!");
        }

        setStatus(StatusEvento.ENCERRADO);
    }

}
