package com.eventos.eventos_api.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eventos.eventos_api.assembler.ListaEsperaAssembler;
import com.eventos.eventos_api.common.Privacidade;
import com.eventos.eventos_api.domain.model.ListaEspera;
import com.eventos.eventos_api.domain.service.ListaEsperaService;
import com.eventos.eventos_api.model.input.ParticipanteInput;
import com.eventos.eventos_api.model.output.ListaEsperaOutput;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/lista-espera")
public class ListaEsperaController {

    private ListaEsperaService listaEsperaService;
    private ListaEsperaAssembler listaEsperaAssembler;

    @GetMapping("/evento/{eventoId}") //Lista a fila do evento, em ordem, com a posição de cada pessoa
    public Page<ListaEsperaOutput> listarDoEvento(@PathVariable Long eventoId, Pageable pageable) {
        Page<ListaEspera> pagina = listaEsperaService.listarDoEvento(eventoId, pageable);
        long inicio = pagina.getPageable().isPaged() ? pagina.getPageable().getOffset() : 0;
        long[] posicao = { inicio };
        boolean admin = Privacidade.usuarioAtualEhAdmin();
        return pagina.map(inscricao -> {
            ListaEsperaOutput output = listaEsperaAssembler.toOutput(inscricao, ++posicao[0]);
            if (!admin) {
                output.setEmail(Privacidade.mascararEmail(output.getEmail())); //USER vê o email mascarado
            }
            return output;
        });
    }

    @GetMapping("/quantidade/{eventoId}") //Quantas pessoas estão na fila do evento
    public Long contarDoEvento(@PathVariable Long eventoId) {
        return listaEsperaService.contarDoEvento(eventoId);
    }

    @PostMapping //Entra na lista de espera de um evento lotado
    @ResponseStatus(HttpStatus.CREATED)
    public ListaEsperaOutput entrar(@Valid @RequestBody ParticipanteInput input) {
        ListaEspera inscricao = listaEsperaService.entrar(input);
        return listaEsperaAssembler.toOutput(inscricao, listaEsperaService.posicao(inscricao));
    }

    @DeleteMapping("/{inscricaoId}") //Remove alguém da lista de espera
    public ResponseEntity<Void> sair(@PathVariable Long inscricaoId) {
        listaEsperaService.sair(inscricaoId);
        return ResponseEntity.noContent().build();
    }
}
