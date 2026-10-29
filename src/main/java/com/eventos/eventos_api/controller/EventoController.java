package com.eventos.eventos_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eventos.eventos_api.assembler.EventoAssembler;
import com.eventos.eventos_api.domain.service.CrudEventoService;

import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.model.StatusEvento;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.apache.poi.ss.formula.functions.Offset;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.eventos.eventos_api.model.input.EventoInput;
import com.eventos.eventos_api.model.output.EventoOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController //Declara a classe como um Rest Controller
@AllArgsConstructor //Gera um constructor com todos os argumentos/variaveis
@RequestMapping("/eventos") //Declara um padrão para o começo da URL de request
public class EventoController {
    
    private CrudEventoService crudEventoService;
    private EventoAssembler eventoAssembler;

    //List de todos os eventos

    //@GetMapping      
    //public List<EventoOutput> listarEventos(){
      //  return eventoAssembler.toCollectionOutput(crudEventoService.buscarTodosEventos());
    //}

    @GetMapping("/{eventoId}") //Método para buscar evento por id
    public EventoOutput listarUnicoEvento(@PathVariable Long eventoId){
        return eventoAssembler.toOutput(crudEventoService.buscarUnicoEvento(eventoId)); //Pega o evento inteiro e manda para Output
    }

    @GetMapping      //Lista os eventos com paginação, aplicando os filtros opcionais (nome, status e período)
    public Page<EventoOutput> listarEventosPaginacao(
        Pageable pageable,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) StatusEvento status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataInicial,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataFinal){
        return eventoAssembler.toCollectionOutput(crudEventoService.buscarComFiltros(nome, status, dataInicial, dataFinal, pageable)); //Pega os eventos filtrados e muda para o formato de Output
    }

    @GetMapping("/filtro/{statusEvento}")
    public Page<EventoOutput> listarEventosStatus(Pageable pageable, @PathVariable StatusEvento statusEvento){
        return eventoAssembler.toCollectionOutput(crudEventoService.buscarEventosStatus(statusEvento, pageable));
    }

    @GetMapping("/periodo")
    public List<EventoOutput> buscarPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataInicial,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataFinal) {

        return eventoAssembler.toCollectionOutput(
            crudEventoService.buscarPeriodo(dataInicial, dataFinal));
    }

    @GetMapping("status")
    public int contarStatus(@RequestParam StatusEvento status) {
        return crudEventoService.contarStatus(status);
    }
    

    @PostMapping //Método para cadastrar evento
    @ResponseStatus(HttpStatus.CREATED)       //Declara a resposta com o status 201 de HTTP  
    public EventoOutput cadastrarEvento(@Valid @RequestBody EventoInput input) { //Valid faz as validações das condições na entity, RequestBody pega o molde do corpo da requisição
    Evento evento = crudEventoService.cadastrar(input); //Pega o input de evento e cadastra o evento
    return eventoAssembler.toOutput(evento); //Transforma o evento em um evento Output para a saída
    }
    
    @PutMapping("/{eventoId}") //Método para atualizar um evento
    public ResponseEntity<EventoOutput> atualizarEvento(@PathVariable Long eventoId, @Valid @RequestBody EventoInput input) { //PathVariable declara que a respectiva variavel é a que altera a URL, Valid faz as validações das condições na entity, RequestBody pega o molde do corpo da requisição
        Evento evento = crudEventoService.atualizar(eventoId, input);  //Atualiza o evento
        return ResponseEntity.ok(eventoAssembler.toOutput(evento)); //Manda o formato do evento para Output para a saída
    }


    @DeleteMapping("/{eventoId}") //Método para exclusão de um evento
    public ResponseEntity<Void> excluirEvento(@PathVariable Long eventoId){ //PathVariable declara que a respectiva variavel é a que altera a URL
        crudEventoService.excluir(eventoId); //exclui o evento
        return ResponseEntity.noContent().build(); //Retorna uma resposta vazia, de código http 204
    }
    
}
