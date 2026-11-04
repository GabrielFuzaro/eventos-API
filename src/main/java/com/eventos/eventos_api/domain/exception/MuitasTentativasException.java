package com.eventos.eventos_api.domain.exception;

public class MuitasTentativasException extends RuntimeException { //Respondida com HTTP 429 (Too Many Requests)

    private static final long serialVersionUID = 1L;

    public MuitasTentativasException(String message) {
        super(message);
    }
}
