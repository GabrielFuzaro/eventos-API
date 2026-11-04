package com.eventos.eventos_api.exceptionHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.exception.MuitasTentativasException;
import com.eventos.eventos_api.domain.exception.NegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{  //Só Deus sabe o que ta rolando aqui nessa classe
    
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private MessageSource messageSource;

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
        HttpHeaders header, HttpStatus status, WebRequest request) {
        List<Problema.Campo> campos = new ArrayList<>();

        for(ObjectError error : ex.getBindingResult().getAllErrors()){
            String nome = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String mensagem = messageSource.getMessage(error, LocaleContextHolder.getLocale());
        
            campos.add(new Problema.Campo(nome, mensagem));
        }

        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setDataHora(OffsetDateTime.now());
        problema.setTitulo("Um ou mais campos estão inválidos, Faça o preenchimento correto e tente novamente.");
        problema.setCampos(campos);

        return handleExceptionInternal(ex, problema, header, status, request);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Object> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, WebRequest request){
    HttpStatus status = HttpStatus.NOT_FOUND;

    Problema problema = new Problema();
    problema.setStatus(status.value());
    problema.setDataHora(OffsetDateTime.now());
    problema.setTitulo(ex.getMessage());

    return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Object> handleNegocioException(NegocioException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setDataHora(OffsetDateTime.now());
        problema.setTitulo(ex.getMessage());

        return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(AuthenticationException.class) //Login com usuário ou senha errados
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request){
        //Mensagem genérica de propósito: não revela se o usuário existe
        return responder(ex, HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request){
        return responder(ex, HttpStatus.FORBIDDEN, "Você não tem permissão para fazer isso.", request);
    }

    @ExceptionHandler(MuitasTentativasException.class)
    public ResponseEntity<Object> handleMuitasTentativasException(MuitasTentativasException ex, WebRequest request){
        return responder(ex, HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class) //Qualquer erro inesperado: registra no log e não expõe detalhes internos
    public ResponseEntity<Object> handleExceptionGenerica(Exception ex, WebRequest request){
        log.error("Erro inesperado", ex);
        return responder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.", request);
    }

    private ResponseEntity<Object> responder(Exception ex, HttpStatus status, String titulo, WebRequest request){
        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setDataHora(OffsetDateTime.now());
        problema.setTitulo(titulo);

        return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
    }
}
