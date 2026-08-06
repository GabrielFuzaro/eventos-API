package com.eventos.eventos_api.domain.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.exception.NegocioExeption;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.model.StatusEvento;

import java.util.List;
import com.eventos.eventos_api.domain.model.Evento;
import javax.transaction.Transactional;

import com.eventos.eventos_api.domain.repository.ParticipanteRepository;
import com.eventos.eventos_api.model.input.ParticipanteInput;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ParticipanteService {
    
    private ParticipanteRepository participanteRepository;
    private CrudEventoService crudEventoService;

    @Transactional
    public List<Participante> listarParticipantes(){
        return participanteRepository.findAll();
    }

    @Transactional
    public Participante listarParticipantePorId(Long participanteId){
        return participanteRepository.findById(participanteId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!"));
    }

    @Transactional
    public Participante cadastrarParticipante(ParticipanteInput input){
        Evento evento = crudEventoService.buscarUnicoEvento(input.getEventoId().getId());
        Participante participante = new Participante();

        participante.setEvento(evento);

        if(evento.getStatus() == StatusEvento.LOTADO){
            throw new NegocioExeption("Esse evento já está lotado!");
        }

        participante.setNome(input.getNome());
        participante.setEmail(input.getEmail());
        
        return participanteRepository.save(participante);
    }

    @Transactional
    public void excluirParticipante(Long participanteId){
        if(!participanteRepository.existsById(participanteId)){
            throw new EntidadeNaoEncontradaException("Participante Não Encontrado!");
        }
        participanteRepository.deleteById(participanteId);
    }
}
