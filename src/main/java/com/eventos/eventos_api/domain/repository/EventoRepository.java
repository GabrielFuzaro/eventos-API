package com.eventos.eventos_api.domain.repository;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.List;
import com.eventos.eventos_api.domain.model.StatusEvento;

import org.apache.poi.ss.formula.functions.Offset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Evento> findByStatus(StatusEvento status, Pageable pageable);
    boolean existsByStatus(StatusEvento status);

    @Query ("Select e from Evento e " + 
        "where e .data_evento >= :dataInicial " +
        "and e.data_evento <= :dataFinal " + 
        "order by e.data_evento")

    List<Evento> buscarEventosPeriodo(
        @Param("dataInicial") OffsetDateTime dataInicial,
        @Param ("dataFinal") OffsetDateTime dataFinal
    );

    @Query ("SELECT e FROM Evento e " +  //Filtros combinados: cada filtro nulo é ignorado
        "WHERE (:nome IS NULL OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
        "AND (:status IS NULL OR e.status = :status) " +
        "AND (:dataInicial IS NULL OR e.data_evento >= :dataInicial) " +
        "AND (:dataFinal IS NULL OR e.data_evento <= :dataFinal)")

    Page<Evento> buscarComFiltros(
        @Param("nome") String nome,
        @Param("status") StatusEvento status,
        @Param("dataInicial") OffsetDateTime dataInicial,
        @Param("dataFinal") OffsetDateTime dataFinal,
        Pageable pageable
    );

    @Query ("SELECT count(e) from Evento e " +
        "Where e.status = :status"
    )
    int contarPorStatus(@Param ("status") StatusEvento status);


}
