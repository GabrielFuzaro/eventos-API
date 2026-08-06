package com.eventos.eventos_api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.eventos.eventos_api.domain.model.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, Long>{
    long countByEventoId(Long eventoId);
    List<Participante> findByEventoId(Long eventoId);
}
