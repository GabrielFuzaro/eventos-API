package com.eventos.eventos_api.controller;

import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.service.CrudEventoService;
import com.eventos.eventos_api.domain.service.ParticipanteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eventos.eventos_api.model.output.ParticipanteOutput;
import com.eventos.eventos_api.assembler.ParticipanteAssembler;
import java.util.List;
import com.eventos.eventos_api.model.input.ParticipanteInput;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@AllArgsConstructor
@RequestMapping("/participantes")
public class ParticipanteController {
    
    private CrudEventoService crudEventoService;
    private ParticipanteService participanteService;
    private ParticipanteAssembler participanteAssembler;


    @GetMapping
    public List<ParticipanteOutput> listarParticipantes(){
        return participanteAssembler.toCollectorsOutput(participanteService.listarParticipantes());
    }

    @GetMapping("/{participanteId}")
    public ParticipanteOutput listarParticipantePorId(@PathVariable Long participanteId){
        return participanteAssembler.toOutput(participanteService.listarParticipantePorId(participanteId));
    }

    @GetMapping("/{eventoId}/participantes")
    public List<ParticipanteOutput> listarParticipantesDoEvento(@PathVariable Long eventoId){
        return participanteAssembler.toCollectorsOutput(crudEventoService.buscarParticipantesDoEvento(eventoId));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participante cadastrarParticipante(@Valid @RequestBody ParticipanteInput input) {
        return participanteService.cadastrarParticipante(input);
    }

    @DeleteMapping("/{participanteId}")
    public ResponseEntity<Participante> excluirParticipante( @PathVariable Long participanteId){
        participanteService.excluirParticipante(participanteId);
        return ResponseEntity.noContent().build();
    }
    
    
}
