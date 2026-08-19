package com.eventos.eventos_api.domain.exception;

public class NegocioExeption extends RuntimeException{  //Cria a classe de NegocioException extendendo as Excessões padrao do Runtime
    
    private static final Long serialVersionUID = 1l;

    public NegocioExeption(String message){
        super(message);
    }
}
