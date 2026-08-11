package com.eventos.eventos_api.assembler;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.model.output.EventoOutput;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class EventoAssembler {
    
    private ModelMapper modelMapper;

    public EventoOutput toOutput(Evento evento){
        return modelMapper.map(evento, EventoOutput.class);
    }

    public List<EventoOutput> toCollectionOutput(List<Evento> evento){
        return evento.stream()
        .map(this::toOutput)
        .collect(Collectors.toList());
    }
}
