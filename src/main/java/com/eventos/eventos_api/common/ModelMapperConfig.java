package com.eventos.eventos_api.common;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //Declara a classe como uma classe de configuração
public class ModelMapperConfig {            //Classe do modelMapper
    
    @Bean
    public ModelMapper modelMapper(){ //O modelMapper facilita a mudança de formato de uma entidade pura para o seu output
        return new ModelMapper(); //É usado no assembler
    }
}
