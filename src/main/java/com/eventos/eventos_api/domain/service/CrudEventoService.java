package com.eventos.eventos_api.domain.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.domain.repository.ParticipanteRepository;
import com.eventos.eventos_api.model.output.EventoOutput;

import antlr.debug.Event;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.exception.NegocioExeption;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.model.StatusEvento;

@AllArgsConstructor
@Service
public class CrudEventoService {
    
    private EventoRepository eventoRepository;
    private ParticipanteRepository participanteRepository;

    public List<Evento> buscarTodosEventos(){
        return eventoRepository.findAll();
    }

    public Evento buscarUnicoEvento(Long eventoId){
        return eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Esse evento não existe!"));
    }

    //Loucura esse metodo aqui mas tenho que estudar ele
    @Transactional
    public List<Participante> buscarParticipantesDoEvento(Long eventoId){
        List<Participante> participantesDoEvento = participanteRepository.findByEventoId(eventoId);

        if (participantesDoEvento.isEmpty()) {
            throw new NegocioExeption("Nenhum participante cadastrado nesse Evento!");
        }

        return participantesDoEvento;
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

        long totalParticipantes = participanteRepository.countByEventoId(eventoId);

        if (totalParticipantes > 0) {
            throw new NegocioExeption("Não é possível excluir um evento que já possui participantes inscritos.");
        }
        
        eventoRepository.deleteById(eventoId);
    }
}
