package com.eventos.eventos_api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.model.output.ParticipanteOutput;

import lombok.AllArgsConstructor;

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

    public Page<ParticipanteOutput> toCollectorsOutput(Page<Participante> participantes){
        return participantes.map(this::toOutput);
    }
}
