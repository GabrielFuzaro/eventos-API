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



@RestController //Declara a classe como um Rest Controller
@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@RequestMapping("/participantes") //Declara um inicio comum para a URL nas requisições
public class ParticipanteController {

    private ParticipanteService participanteService;
    private ParticipanteAssembler participanteAssembler;


    //@GetMapping
    //public List<ParticipanteOutput> listarParticipantes(){
      //return participanteAssembler.toCollectorsOutput(participanteService.listarParticipantes());
    //}

    @GetMapping      //Lista todos os participantes só que com paginação
    public Page<ParticipanteOutput> listarParticipantesPaginacao(Pageable pageable){
        return participanteAssembler.toCollectorsOutput(participanteService.listarTodosPaginacao(pageable)); //Transforma o corpo de cada participante em um corpo de Output 
    }

    @GetMapping("/{participanteId}") //Busca apenas um participante pelo id
    public ParticipanteOutput listarParticipantePorId(@PathVariable Long participanteId){ //PathVariable declara que a respectiva variavel é a que altera a URL
        return participanteAssembler.toOutput(participanteService.listarParticipantePorId(participanteId)); //Transforma o corpo do participante no corpo de output
    }

    @GetMapping("/evento/{eventoId}") //Método para listar participantes de um respectivo evento só que com paginação
    public Page<ParticipanteOutput> listarParticipantesDoEvento(@PathVariable Long eventoId, Pageable pageable){ //PathVariable declara que a respectiva variavel é a que altera a URL
        return participanteAssembler.toCollectorsOutput(participanteService.buscarParticipantesDoEvento(eventoId, pageable)); //Transforma o corpo de cada participante para o formato de output
    }


    @PostMapping //Método para cadastrar participante
    @ResponseStatus(HttpStatus.CREATED) //Retorna um http de status 201 na resposta
    public ParticipanteOutput cadastrarParticipante(@Valid @RequestBody ParticipanteInput input) { //Valid faz as validações das condições na entity, RequestBody pega o molde do corpo da requisição
        Participante participante = participanteService.cadastrarParticipante(input); //Cadastra o participante
        return participanteAssembler.toOutput(participante); //Transforma o corpo de participante para o formato de output
    }

    @DeleteMapping("/{participanteId}") //Método para excluir participante 
    public ResponseEntity<Void> excluirParticipante( @PathVariable Long participanteId){ //PathVariable declara que a respectiva variavel é a que altera a URL
        participanteService.excluirParticipante(participanteId); //Exclui o participante
        return ResponseEntity.noContent().build(); //Retorna uma resposta vazia de metodo http 204
    }
    
    
}
