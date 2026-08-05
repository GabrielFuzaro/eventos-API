package com.eventos.eventos_api.domain.exception;

public class NegocioExeption extends RuntimeException{
    
    private static final Long serialVersionUID = 1l;

    public NegocioExeption(String message){
        super(message);
    }
}
