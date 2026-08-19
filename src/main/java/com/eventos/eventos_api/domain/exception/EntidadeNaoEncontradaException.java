package com.eventos.eventos_api.domain.exception;

public class EntidadeNaoEncontradaException extends RuntimeException{ //Cria a classe de EntidadeNaoEncontradaException extendendo as Excessões padrao do Runtime
    
    private static final Long serialVersionUID = 1l;

    public EntidadeNaoEncontradaException(String message){
        super(message);
    }
}
