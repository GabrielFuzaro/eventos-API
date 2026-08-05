package com.eventos.eventos_api.domain.service;

import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.model.output.EventoOutput;

import antlr.debug.Event;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.model.StatusEvento;

@AllArgsConstructor
@Service
public class CrudEventoService {
    
    private EventoRepository eventoRepository;

    public List<Evento> buscarTodosEventos(){
        return eventoRepository.findAll();
    }

    public Evento buscarUnicoEvento(Long eventoId){
        return eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Esse evento não existe!"));
    }

    @Transactional
    public Evento cadastrar(Evento evento){
        evento.setStatus(StatusEvento.ABERTO);
        evento.setData(OffsetDateTime.now());
        return eventoRepository.save(evento);
    }

    @Transactional
    public void atualizar(Long eventoId){
        if(!eventoRepository.existsById(eventoId)){
            throw new EntidadeNaoEncontradaException("Evento não encontrado!");
        }
    }

    @Transactional
    public void excluir(Long eventoId){
        if(!eventoRepository.existsById(eventoId)){
            throw new EntidadeNaoEncontradaException("Evento não encontrado!");
        }
        eventoRepository.deleteById(eventoId);
    }
}
