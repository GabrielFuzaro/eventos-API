package com.eventos.eventos_api.domain.exception;

public class EntidadeNaoEncontradaException extends RuntimeException{
    
    private static final Long serialVersionUID = 1l;

    public EntidadeNaoEncontradaException(String message){
        super(message);
    }
}
