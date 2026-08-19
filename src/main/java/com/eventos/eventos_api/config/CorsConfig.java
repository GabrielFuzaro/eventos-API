package com.eventos.eventos_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration                 //Declara a classe como uma classe de configuração
public class CorsConfig {          //Muita loucura

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") //Permite a respsctiva URl a mandar requisições para a API
                        .allowedMethods("*");
            }

        };
    }
}
