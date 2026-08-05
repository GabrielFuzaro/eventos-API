package com.eventos.eventos_api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventos.eventos_api.domain.model.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, Long>{
    
}
