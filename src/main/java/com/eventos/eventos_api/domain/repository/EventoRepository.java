package com.eventos.eventos_api.domain.repository;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.eventos.eventos_api.domain.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long>{ //o JPA faz as querys automaticas para o banco de dados
    
    @Query("SELECT e FROM Evento e " +
    "WHERE e.data_evento < :agora " +
    "AND (e.status = 'ABERTO' OR e.status = 'LOTADO')")  //Query para pegar os status dos eventos

    List<Evento> buscarEventosParaEncerrar(   //Busca para verificar quais eventos devem ser encerrados
        @Param("agora") OffsetDateTime agora
    );


}
