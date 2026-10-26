package com.eventos.eventos_api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.model.output.UsuarioOutput;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Component 
public class UsuarioAssembler {
    
    private ModelMapper modelMapper;

    public UsuarioOutput toOutput(Usuario usuario){
        return modelMapper.map(usuario, UsuarioOutput.class);
    }
}
