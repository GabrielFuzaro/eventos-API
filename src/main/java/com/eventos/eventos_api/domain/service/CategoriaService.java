package com.eventos.eventos_api.domain.service;

import com.eventos.eventos_api.domain.exception.EntidadeNaoEncontradaException;
import com.eventos.eventos_api.domain.model.Categoria;
import com.eventos.eventos_api.domain.repository.CategoriaRepository;
import com.eventos.eventos_api.model.input.CategoriaInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria buscarUnicaCategoria(Long categoriaId){
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria nao encontrada"));
    }

    @Transactional
        public List<Categoria> listarTodasCatorias(){
            return categoriaRepository.findAll();
    }
}
