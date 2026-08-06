package com.eventos.eventos_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventos.eventos_api.assembler.EventoAssembler;
import com.eventos.eventos_api.domain.repository.EventoRepository;
import com.eventos.eventos_api.domain.service.CrudEventoService;
import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import com.eventos.eventos_api.domain.model.Evento;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eventos.eventos_api.model.output.EventoOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/eventos")
public class EventoController {
    
    private CrudEventoService crudEventoService;
    private EventoRepository eventoRepository;
    private EventoAssembler eventoAssembler;

    @GetMapping
    public List<EventoOutput> listarEventos(){
        return eventoAssembler.toCollectionOutput(crudEventoService.buscarTodosEventos());
    }

    @GetMapping("/{eventoId}")
    public EventoOutput listarUnicoEvento(@PathVariable Long eventoId){
        return eventoAssembler.toOutput(crudEventoService.buscarUnicoEvento(eventoId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento cadastrarEvento(@Valid @RequestBody Evento evento) {
        return crudEventoService.cadastrar(evento);
    }
    
    @PutMapping("/{eventoId}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable Long eventoId, @Valid @RequestBody Evento evento) {
        //TODO: process PUT request
        crudEventoService.atualizar(eventoId);
        evento.setId(eventoId);
        evento = crudEventoService.cadastrar(evento);
        return ResponseEntity.ok(evento);
    }

    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Evento> excluirEvento(@PathVariable Long eventoId){
        crudEventoService.excluir(eventoId);
        return ResponseEntity.noContent().build();
    }
    
}
