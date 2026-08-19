package com.eventos.eventos_api.assembler;

import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.model.output.EventoOutput;

import lombok.AllArgsConstructor;

@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@Component //Declara a classe como um componente
public class EventoAssembler {
    
    private ModelMapper modelMapper;

    public EventoOutput toOutput(Evento evento){ //Recebe um evento e transforma para o formato de output
        return modelMapper.map(evento, EventoOutput.class);
    }

    public Page<EventoOutput> toCollectionOutput(Page<Evento> evento){ //Recebe uma lista de eventos e transforma cada um no formato do output
        return evento.map(this::toOutput);
    }
}
