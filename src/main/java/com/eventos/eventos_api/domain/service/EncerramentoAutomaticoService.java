package com.eventos.eventos_api.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.repository.EventoRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@AllArgsConstructor //Gera um constructor com todos os artumentos/Variaveis
@Service //Declara a classe como um service
public class EncerramentoAutomaticoService {

    private EventoRepository eventoRepository;

    @Scheduled(fixedRate = 60000) //Agenda uma verificação de alguma condição no tempo que esta no fixedrate
    @Transactional 
    public void encerrarEventos() {           //Método para encerrar eventos

        List<Evento> eventos = eventoRepository.buscarEventosParaEncerrar(
            OffsetDateTime.now()                    //Gera uma lista de eventos que podem ser encerrados
        );

        eventos.forEach(Evento::encerrar);  //Executa o método de encerrar para cada evento da lista

        eventoRepository.saveAll(eventos);  //Salva os eventos alterados 
    }
}
