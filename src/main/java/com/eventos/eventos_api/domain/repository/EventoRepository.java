package com.eventos.eventos_api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eventos.eventos_api.domain.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long>{
    
}
