package com.eventos.eventos_api.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration                 //Declara a classe como uma classe de configuração
public class CorsConfig {

    @Value("${cors.origens-permitidas:http://localhost:4200}")
    private String origensPermitidas; //Separadas por vírgula

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { //Usado pelo Spring Security (http.cors())
        CorsConfiguration config = new CorsConfiguration();
        //Origem exata (nunca "*"): obrigatório para enviar cookies, e requisições de outras origens são recusadas
        config.setAllowedOrigins(Arrays.stream(origensPermitidas.split(","))
            .map(String::trim).filter(o -> !o.isEmpty()).collect(Collectors.toList()));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        config.setExposedHeaders(Arrays.asList("Content-Disposition")); //Nome do arquivo do Excel
        config.setAllowCredentials(true); //Permite o envio do cookie do token
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
