package com.eventos.eventos_api.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventos.eventos_api.domain.model.ListaEspera;

public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {

    Page<ListaEspera> findByEventoIdOrderByIdAsc(Long eventoId, Pageable pageable); //Fila do evento em ordem de chegada (id crescente)

    Optional<ListaEspera> findFirstByEventoIdOrderByIdAsc(Long eventoId); //Primeiro da fila

    long countByEventoId(Long eventoId);

    boolean existsByEmailAndEventoId(String email, Long eventoId);

    //O id é auto incremento, então quem tem id menor entrou antes na fila
    long countByEventoIdAndIdLessThan(Long eventoId, Long id); //Quantas pessoas estão na frente de uma inscrição

    @Modifying
    @Query("DELETE FROM ListaEspera l WHERE l.evento.id = :eventoId")
    void excluirPorEvento(@Param("eventoId") Long eventoId);
}
