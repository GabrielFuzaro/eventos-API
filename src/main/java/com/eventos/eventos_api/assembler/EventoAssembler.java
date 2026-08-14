package com.eventos.eventos_api.assembler;

import org.springframework.data.domain.Page;
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

    public Page<EventoOutput> toCollectionOutput(Page<Evento> evento){
        return evento.map(this::toOutput);
    }
}
