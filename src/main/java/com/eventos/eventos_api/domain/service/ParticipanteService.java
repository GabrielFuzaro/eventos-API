package com.eventos.eventos_api.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@Service //Declara a classe como um service
public class ParticipanteService {
    
    private ParticipanteRepository participanteRepository;
    private CrudEventoService crudEventoService;

    @Transactional
    public List<Participante> listarParticipantes(){  //Método para listar todos os participantes
        return participanteRepository.findAll();
    }

    public Page<Participante> listarTodosPaginacao(Pageable pageable){  //Método oara listar todos os partiipantes só que com paginação
        return participanteRepository.findAll(pageable);
    }

    @Transactional
    public Participante listarParticipantePorId(Long participanteId){ //Método para buscar um partiicpante pelo id
        return participanteRepository.findById(participanteId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!")); //Lança exception se o participante nao existir
    }

    @Transactional
    public Participante cadastrarParticipante(ParticipanteInput input){   //Registra um participante através do input de participante
        Evento evento = crudEventoService.buscarUnicoEvento(input.getEventoId().getId());  //Busca o evento que o participante será cadastrado
        Participante participante = new Participante(); //Cria um participante novo com tudo vazio

        if (evento.getStatus() == StatusEvento.LOTADO) {
            throw new NegocioExeption("Esse evento já está lotado!"); //Verifica se o evento já está lotado para nao receber mais participantes
        }

        participante.setEvento(evento); //Passa o evento para o participante
        participante.setNome(input.getNome()); //registra o nome do participante através do input
        participante.setEmail(input.getEmail()); //registra o email do particiipante atravéd do input

        Participante participanteSalvo = participanteRepository.save(participante); //Salva o participante no banco

        long totalInscritos = participanteRepository.countByEventoId(evento.getId()); //Verifica quantos participantes estão cadastrados no evento

        if (totalInscritos >= evento.getCapacidade_maxima()) {
            evento.setStatus(StatusEvento.LOTADO);             //Muda o evento para lotado caso o total de participantes se iguale ou ultrapasse a capacidade maxima do evento
        }
        if(totalInscritos < evento.getCapacidade_maxima()){
            evento.setStatus(StatusEvento.ABERTO); //Muda o evento para aberto caso o total de participantes seja menor que a capacidade maxima do evento
        }

        return participanteSalvo;
    }

    @Transactional
    public void excluirParticipante(Long participanteId){ //Método para excluir um praticipante
        Participante participante = participanteRepository.findById(participanteId) //Procura um participante pelo id
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Participante Não Encontrado!")); //Lança exception caso o participante nao exista

        Evento evento = participante.getEvento(); //pega o evento do evento do participante

        participanteRepository.deleteById(participanteId); //exclui o participante pelo id

        if (evento.getStatus() == StatusEvento.LOTADO) {
            evento.setStatus(StatusEvento.ABERTO); //Muda o status para aberto caso após a exclusão caso o evento esteja lotado
        }
    }

    @Transactional
    public Page<Participante> buscarParticipantesDoEvento(Long eventoId, Pageable pageable){ //Lista os participantes do evento com paginação
        Page<Participante> participantesDoEvento = participanteRepository.findByEventoId(eventoId, pageable);

        if (participantesDoEvento.isEmpty()) {
            throw new NegocioExeption("Nenhum participante cadastrado nesse Evento!"); //Lanca exception caso nao haja nenhum participante cadastrado no evento
        }

        return participantesDoEvento;
    }
}
