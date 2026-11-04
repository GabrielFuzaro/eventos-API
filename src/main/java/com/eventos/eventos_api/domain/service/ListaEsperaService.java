package com.eventos.eventos_api.domain.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.exception.NegocioException;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.model.ListaEspera;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.model.StatusEvento;
import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.domain.repository.ListaEsperaRepository;
import com.eventos.eventos_api.domain.repository.ParticipanteRepository;
import com.eventos.eventos_api.model.input.ParticipanteInput;

import org.springframework.context.ApplicationEventPublisher;
import com.eventos.eventos_api.domain.event.NotificacaoEmailEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ListaEsperaService { //Regras da lista de espera dos eventos

    //Usa os repositories direto (e não o CrudEventoService) para evitar dependência circular,
    //já que o CrudEventoService também usa este service
    private ListaEsperaRepository listaEsperaRepository;
    private ParticipanteRepository participanteRepository;
    private EventoRepository eventoRepository;
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public ListaEspera entrar(ParticipanteInput input) { //Coloca uma pessoa na fila de um evento lotado
        if (input.getEventoId() == null || input.getEventoId().getId() == null) {
            throw new NegocioException("Informe o evento!");
        }

        Evento evento = eventoRepository.findById(input.getEventoId().getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Esse evento não existe!"));

        if (evento.jaOcorreu()) {
            throw new NegocioException("Esse evento já ocorreu, não é possível entrar na lista de espera!");
        }

        long inscritos = participanteRepository.countByEventoId(evento.getId());
        if (inscritos < evento.getCapacidade_maxima()) {
            throw new NegocioException("Ainda há vagas nesse evento, faça a inscrição normalmente!");
        }

        if (participanteRepository.existsByEmailAndEventoId(input.getEmail(), evento.getId())) {
            throw new NegocioException("Esse email já está inscrito nesse evento!");
        }

        if (listaEsperaRepository.existsByEmailAndEventoId(input.getEmail(), evento.getId())) {
            throw new NegocioException("Esse email já está na lista de espera desse evento!");
        }

        ListaEspera inscricao = new ListaEspera();
        inscricao.setEvento(evento);
        inscricao.setNome(input.getNome());
        inscricao.setEmail(input.getEmail());
        inscricao.setDataInscricao(OffsetDateTime.now());

        ListaEspera salva = listaEsperaRepository.save(inscricao);

        //Email informando a posição na fila (enviado só depois do commit, em segundo plano)
        eventPublisher.publishEvent(NotificacaoEmailEvent.entradaListaEspera(salva.getNome(), salva.getEmail(), evento, posicao(salva)));

        return salva;
    }

    public long posicao(ListaEspera inscricao) { //Posição da inscrição na fila (1 = próximo a ser chamado)
        return listaEsperaRepository.countByEventoIdAndIdLessThan(inscricao.getEvento().getId(), inscricao.getId()) + 1;
    }

    public Page<ListaEspera> listarDoEvento(Long eventoId, Pageable pageable) { //Fila do evento em ordem de chegada
        if (!eventoRepository.existsById(eventoId)) {
            throw new EntidadeNaoEncontradaException("Esse evento não existe!");
        }
        return listaEsperaRepository.findByEventoIdOrderByIdAsc(eventoId, pageable);
    }

    public long contarDoEvento(Long eventoId) {
        return listaEsperaRepository.countByEventoId(eventoId);
    }

    public boolean estaNaLista(String email, Long eventoId) {
        return listaEsperaRepository.existsByEmailAndEventoId(email, eventoId);
    }

    @Transactional
    public void sair(Long inscricaoId) { //Remove alguém da lista de espera
        ListaEspera inscricao = listaEsperaRepository.findById(inscricaoId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Inscrição na lista de espera não encontrada!"));
        listaEsperaRepository.delete(inscricao);
    }

    @Transactional
    public void limparDoEvento(Long eventoId) { //Apaga a fila inteira do evento (usado antes de excluir o evento)
        listaEsperaRepository.excluirPorEvento(eventoId);
    }

    /**
     * Preenche as vagas livres do evento com as pessoas da lista de espera, na ordem de chegada,
     * e atualiza o status (ABERTO / LOTADO) de acordo com a nova quantidade de participantes.
     * Só promove se o evento ainda não ocorreu. Retorna os participantes que saíram da fila.
     */
    @Transactional
    public List<Participante> preencherVagas(Evento evento) {
        List<Participante> promovidos = new ArrayList<>();
        long inscritos = participanteRepository.countByEventoId(evento.getId());

        if (!evento.jaOcorreu()) {
            while (inscritos < evento.getCapacidade_maxima()) {
                Optional<ListaEspera> primeiro = listaEsperaRepository.findFirstByEventoIdOrderByIdAsc(evento.getId());
                if (!primeiro.isPresent()) {
                    break; //Fila vazia
                }

                ListaEspera inscricao = primeiro.get();

                Participante participante = new Participante(); //O primeiro da fila vira participante
                participante.setEvento(evento);
                participante.setNome(inscricao.getNome());
                participante.setEmail(inscricao.getEmail());
                promovidos.add(participanteRepository.save(participante));

                listaEsperaRepository.delete(inscricao); //E sai da fila

                //Email avisando que a vaga foi confirmada
                eventPublisher.publishEvent(NotificacaoEmailEvent.vagaLiberada(participante.getNome(), participante.getEmail(), evento));
                inscritos++;
            }
        }

        if (evento.getStatus() != StatusEvento.ENCERRADO) {
            evento.setStatus(inscritos >= evento.getCapacidade_maxima() ? StatusEvento.LOTADO : StatusEvento.ABERTO);
        }

        return promovidos;
    }
}
