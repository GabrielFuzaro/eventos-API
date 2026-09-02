package com.eventos.eventos_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita proteção CSRF — explicado no porquê logo abaixo
            .csrf().disable()

            // Define quais rotas exigem autenticação e quais são livres
            .authorizeHttpRequests(auth -> auth
                // Toda requisição OPTIONS é liberada (necessário pro CORS funcionar — ver Parte 9)
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Leitura de eventos é pública, qualquer um pode ver
                .antMatchers(HttpMethod.GET, "/eventos/**").permitAll()

                // Criar, editar e excluir evento exige estar autenticado
                .antMatchers(HttpMethod.POST, "/eventos/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/eventos/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/eventos/**").authenticated()

                // O endpoint de login precisa ficar público (senão ninguém consegue logar)
                .antMatchers("/auth/**").permitAll()

                // Qualquer outra rota não listada acima: por padrão, deixamos pública
                // (você pode trocar para .authenticated() se preferir bloquear tudo que não foi listado)
                .anyRequest().permitAll()
            )

            // Ativa autenticação via usuário/senha no header (Basic Auth) — usado só na Parte 4,
            // depois trocado por JWT na Parte 6 em diante
            .httpBasic();

        return http.build();
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
