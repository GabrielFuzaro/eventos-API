package com.eventos.eventos_api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.model.output.EventoOutput;
import com.eventos.eventos_api.model.output.ParticipanteOutput;

import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Component
@AllArgsConstructor
public class ParticipanteAssembler {
    
    private ModelMapper modelMapper;
    private EventoAssembler eventoAssembler;

    public ParticipanteOutput toOutput(Participante participante){
        ParticipanteOutput output = modelMapper.map(participante, ParticipanteOutput.class);
        output.setEvento(eventoAssembler.toOutput(participante.getEvento()));

        return output;
    }

    public List<ParticipanteOutput> toCollectorsOutput(List<Participante> participantes){
        return participantes.stream()
        .map(this::toOutput)
        .collect(Collectors.toList());
    }
}
