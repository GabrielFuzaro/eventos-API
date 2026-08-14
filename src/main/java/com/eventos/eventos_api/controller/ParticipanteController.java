package com.eventos.eventos_api.controller;

import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.domain.service.ParticipanteService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eventos.eventos_api.model.output.ParticipanteOutput;
import com.eventos.eventos_api.assembler.ParticipanteAssembler;
import com.eventos.eventos_api.model.input.ParticipanteInput;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@AllArgsConstructor
@RequestMapping("/participantes")
public class ParticipanteController {

    private ParticipanteService participanteService;
    private ParticipanteAssembler participanteAssembler;


    //@GetMapping
    //public List<ParticipanteOutput> listarParticipantes(){
      //return participanteAssembler.toCollectorsOutput(participanteService.listarParticipantes());
    //}

    @GetMapping
    public Page<ParticipanteOutput> listarParticipantesPaginacao(Pageable pageable){
        return participanteAssembler.toCollectorsOutput(participanteService.listarTodosPaginacao(pageable));
    }

    @GetMapping("/{participanteId}")
    public ParticipanteOutput listarParticipantePorId(@PathVariable Long participanteId){
        return participanteAssembler.toOutput(participanteService.listarParticipantePorId(participanteId));
    }

    @GetMapping("/evento/{eventoId}")
    public Page<ParticipanteOutput> listarParticipantesDoEvento(@PathVariable Long eventoId, Pageable pageable){
        return participanteAssembler.toCollectorsOutput(participanteService.buscarParticipantesDoEvento(eventoId, pageable));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipanteOutput cadastrarParticipante(@Valid @RequestBody ParticipanteInput input) {
        Participante participante = participanteService.cadastrarParticipante(input);
        return participanteAssembler.toOutput(participante);
    }

    @DeleteMapping("/{participanteId}")
    public ResponseEntity<Void> excluirParticipante( @PathVariable Long participanteId){
        participanteService.excluirParticipante(participanteId);
        return ResponseEntity.noContent().build();
    }
    
    
}
