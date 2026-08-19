package com.eventos.eventos_api.domain.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
import com.eventos.eventos_api.domain.model.StatusEvento;

@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@Service //Declara classe como um service
public class CrudEventoService {
    
    private EventoRepository eventoRepository;
    private ParticipanteRepository participanteRepository;

    public List<Evento> buscarTodosEventos(){ //Busca todos os eventos
        return eventoRepository.findAll();
    }

    public Evento buscarUnicoEvento(Long eventoId){  //Busca apenas um evento através do id
        return eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Esse evento não existe!"));
    }

    public Page<Evento> listarEventosPaginacao(Pageable pageable) { //Busca todos os eventos mas como paginação
        return eventoRepository.findAll(pageable);
    }
    
    @Transactional
    public Evento cadastrar(EventoInput input){       //Cadastra um evento através de um Input de evento
        Evento evento = new Evento();

        evento.setNome(input.getNome());
        evento.setData_evento(input.getData_evento());
        evento.setCapacidade_maxima(input.getCapacidade_maxima());    //Seta as coisas do evento através do que foi declarado no input
        evento.setLocal(input.getLocal());
        evento.setStatus(StatusEvento.ABERTO);
        evento.setData(OffsetDateTime.now());

        if(evento.getData_evento().isBefore(OffsetDateTime.now())) {
            throw new NegocioExeption("Não é permitido cadastrar eventos no passado!"); //Proíbe cadastro de datas de eventos no passado
        }

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizar(Long eventoId, EventoInput input){     //Método para atualizar um evento
        Evento evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento não encontrado")); //Joga excessão caso o evento nao exista

        evento.setNome(input.getNome());
        evento.setLocal(input.getLocal());
        evento.setCapacidade_maxima(input.getCapacidade_maxima()); //Seta as coisas do evento de acordo com o que foi inserido no input
        evento.setData_evento(input.getData_evento());
        
        long quantidadeDeParticipantes = participanteRepository.countByEventoId(eventoId); //Conta quantos participantes tem nesse respectivo evento

        if(quantidadeDeParticipantes >= evento.getCapacidade_maxima()){
            evento.setStatus(StatusEvento.LOTADO);
        } else {                                                 //Muda o status do evento com base na quantidade de participantes
            evento.setStatus(StatusEvento.ABERTO);
        }

        if(evento.getData_evento().isBefore(OffsetDateTime.now())) {
            throw new NegocioExeption("Não é permitido cadastrar eventos no passado!"); //Pro[ibe eventos no passado
        }

        return eventoRepository.save(evento); //Salva o resultado no banco
    }

    @Transactional
    public Evento encerrar(Long eventoId, EventoInput input){           //Método para encerrar evento
        Evento evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento não encontrado")); //Joga exception se o evento nao existe

        evento.setNome(input.getNome());
        evento.setLocal(input.getLocal());
        evento.setCapacidade_maxima(input.getCapacidade_maxima());     //Seta as coisas do evento de acordo com o input
        evento.setData_evento(input.getData_evento());
        evento.setStatus(StatusEvento.ENCERRADO);

        return eventoRepository.save(evento); //Salva o resultado no banco
    }

    @Transactional
    public void excluir(Long eventoId){                              //Método para excluir um evento
        if(!eventoRepository.existsById(eventoId)){
            throw new EntidadeNaoEncontradaException("Evento não encontrado!"); //Lança exception caso o evento nao exista
        }

        long totalParticipantes = participanteRepository.countByEventoId(eventoId); //Conta os participantes que o respectivo evento possui

        if (totalParticipantes > 0) {
            throw new NegocioExeption("Não é possível excluir um evento que já possui participantes inscritos."); //Se tiver participantes cadastrados no evento, lança uma exception para nao permitir a exclusão
        }
        
        eventoRepository.deleteById(eventoId); // Manda a requisição de delete
    }

}
