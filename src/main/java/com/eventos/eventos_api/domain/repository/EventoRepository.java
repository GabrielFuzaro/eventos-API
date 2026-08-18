package com.eventos.eventos_api.domain.repository;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.eventos.eventos_api.domain.model.Evento;
import com.eventos.eventos_api.domain.model.StatusEvento;

public interface EventoRepository extends JpaRepository<Evento, Long>{
    
    @Query("SELECT e FROM Evento e " +
    "WHERE e.data_evento < :agora " +
    "AND e.status = :status")

    List<Evento> buscarEventosParaEncerrar(
        @Param("agora") OffsetDateTime agora,
        @Param("status") StatusEvento status
    );


}
