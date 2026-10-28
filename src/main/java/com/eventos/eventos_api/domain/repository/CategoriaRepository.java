package com.eventos.eventos_api.domain.repository;

import com.eventos.eventos_api.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
