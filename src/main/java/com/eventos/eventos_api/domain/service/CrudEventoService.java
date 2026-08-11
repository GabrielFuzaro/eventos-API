package com.eventos.eventos_api.domain.service;
import org.springframework.stereotype.Service;
import com.eventos.eventos_api.model.input.EventoInput;
import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.domain.repository.ParticipanteRepository;
import java.time.OffsetDateTime;
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
    public Evento cadastrar(EventoInput input){
        Evento evento = new Evento();

        evento.setNome(input.getNome());
        evento.setData_evento(input.getData_evento());
        evento.setCapacidade_maxima(input.getCapacidade_maxima());
        evento.setLocal(input.getLocal());
        evento.setStatus(StatusEvento.ABERTO);
        evento.setData(OffsetDateTime.now());

        if(evento.getData_evento().isBefore(OffsetDateTime.now())) {
            throw new NegocioExeption("Não é permitido cadastrar eventos no passado!");
        }

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizar(Long eventoId, EventoInput input){
        Evento evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento não encontrado"));

        evento.setNome(input.getNome());
        evento.setLocal(input.getLocal());
        evento.setCapacidade_maxima(input.getCapacidade_maxima());
        evento.setData_evento(input.getData_evento());

        return eventoRepository.save(evento);
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
