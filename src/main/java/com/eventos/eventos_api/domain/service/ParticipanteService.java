package com.eventos.eventos_api.domain.service;

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

        if (evento.getStatus() == StatusEvento.LOTADO) {
            throw new NegocioExeption("Esse evento já está lotado!");
        }

        participante.setEvento(evento);
        participante.setNome(input.getNome());
        participante.setEmail(input.getEmail());

        Participante participanteSalvo = participanteRepository.save(participante);

        long totalInscritos = participanteRepository.countByEventoId(evento.getId());

        if (totalInscritos >= evento.getCapacidade_maxima()) {
            evento.setStatus(StatusEvento.LOTADO);
        }

        return participanteSalvo;
    }

    @Transactional
    public void excluirParticipante(Long participanteId){
        Participante participante = participanteRepository.findById(participanteId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!"));

        Evento evento = participante.getEvento();

        participanteRepository.deleteById(participanteId);

        if (evento.getStatus() == StatusEvento.LOTADO) {
            evento.setStatus(StatusEvento.ABERTO);
        }
    }

    @Transactional
    public List<Participante> buscarParticipantesDoEvento(Long eventoId){
        List<Participante> participantesDoEvento = participanteRepository.findByEventoId(eventoId);

        if (participantesDoEvento.isEmpty()) {
            throw new NegocioExeption("Nenhum participante cadastrado nesse Evento!");
        }

        return participantesDoEvento;
    }
}
