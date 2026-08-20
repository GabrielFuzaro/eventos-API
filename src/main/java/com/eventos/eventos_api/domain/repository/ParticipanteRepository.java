package com.eventos.eventos_api.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eventos.eventos_api.domain.model.Participante;


public interface ParticipanteRepository extends JpaRepository<Participante, Long>{ // O JPA faz as querys automaticas para o banco de dados
    long countByEventoId(Long eventoId); //Método para contar quantos participantes existem em um determinado evento
    Page<Participante> findByEventoId(Long eventoId, Pageable pageable); //Método para a paginação de participantes de um determinado evento
    boolean existsByEmailAndEventoId(String email, Long eventoId); //Método para verificar existência de email
}
