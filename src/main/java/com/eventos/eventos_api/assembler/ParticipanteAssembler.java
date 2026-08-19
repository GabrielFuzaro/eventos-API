package com.eventos.eventos_api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.eventos.eventos_api.domain.model.Participante;
import com.eventos.eventos_api.model.output.ParticipanteOutput;

import lombok.AllArgsConstructor;

@Component    //Declara a classe como um componente
@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
public class ParticipanteAssembler {
    
    private ModelMapper modelMapper;
    private EventoAssembler eventoAssembler;

    public ParticipanteOutput toOutput(Participante participante){ //Recebe um Participante e transforma no formato de Output
        ParticipanteOutput output = modelMapper.map(participante, ParticipanteOutput.class); //Usa o ModelMapper para facilitar
        output.setEvento(eventoAssembler.toOutput(participante.getEvento())); //Pega o evento do participante e transforma no formato de output tammbem

        return output;
    }

    public Page<ParticipanteOutput> toCollectorsOutput(Page<Participante> participantes){ //Recebe uma lista de participante e transforma cada um no formato do output
        return participantes.map(this::toOutput);
    }
}
