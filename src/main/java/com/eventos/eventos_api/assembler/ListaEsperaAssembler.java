package com.eventos.eventos_api.assembler;

import org.springframework.stereotype.Component;

import com.eventos.eventos_api.domain.model.ListaEspera;
import com.eventos.eventos_api.model.output.ListaEsperaOutput;

@Component
public class ListaEsperaAssembler {

    public ListaEsperaOutput toOutput(ListaEspera inscricao, long posicao) {
        ListaEsperaOutput output = new ListaEsperaOutput();
        output.setId(inscricao.getId());
        output.setEventoId(inscricao.getEvento().getId());
        output.setNome(inscricao.getNome());
        output.setEmail(inscricao.getEmail());
        output.setDataInscricao(inscricao.getDataInscricao());
        output.setPosicao(posicao);
        return output;
    }
}
