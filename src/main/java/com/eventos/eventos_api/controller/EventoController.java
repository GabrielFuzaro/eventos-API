package com.eventos.eventos_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eventos.eventos_api.assembler.EventoAssembler;
import com.eventos.eventos_api.domain.service.CrudEventoService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import com.eventos.eventos_api.domain.model.Evento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.eventos.eventos_api.model.input.EventoInput;
import com.eventos.eventos_api.model.output.EventoOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/eventos")
public class EventoController {
    
    private CrudEventoService crudEventoService;
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
    public EventoOutput cadastrarEvento(@Valid @RequestBody EventoInput input) {
    Evento evento = crudEventoService.cadastrar(input);
    return eventoAssembler.toOutput(evento);
    }
    
    @PutMapping("/{eventoId}")
    public ResponseEntity<EventoOutput> atualizarEvento(@PathVariable Long eventoId, @Valid @RequestBody EventoInput input) {
        //TODO: process PUT request
        Evento evento = crudEventoService.atualizar(eventoId, input);
        return ResponseEntity.ok(eventoAssembler.toOutput(evento));
    }

    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> excluirEvento(@PathVariable Long eventoId){
        crudEventoService.excluir(eventoId);
        return ResponseEntity.noContent().build();
    }
    
}
