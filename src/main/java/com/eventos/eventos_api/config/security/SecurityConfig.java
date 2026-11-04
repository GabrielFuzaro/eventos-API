package com.eventos.eventos_api.config.security;

import java.io.IOException;
import java.time.OffsetDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

import com.eventos.eventos_api.exceptionHandler.Problema;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF desabilitado de propósito: a API é stateless e o cookie do token é SameSite=Strict
            // (o navegador não o envia a partir de outros sites). Além disso, o CORS recusa
            // requisições cujo Origin não esteja na lista de origens permitidas.
            .csrf().disable()
            .cors().and()

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Respostas JSON (formato Problema) para 401 e 403, em vez da tela/popup padrão
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) ->
                    escreverProblema(response, HttpServletResponse.SC_UNAUTHORIZED, "Faça login para continuar."))
                .accessDeniedHandler((request, response, e) ->
                    escreverProblema(response, HttpServletResponse.SC_FORBIDDEN, "Você não tem permissão para fazer isso.")))

            // Cabeçalhos de segurança (além dos padrões do Spring: nosniff, X-Frame-Options, cache-control)
            .headers(headers -> headers
                .contentSecurityPolicy("default-src 'none'; frame-ancestors 'none'").and()
                .referrerPolicy(ReferrerPolicy.NO_REFERRER))

            // Define quais rotas exigem autenticação e quais são livres (a primeira regra que casar vale)
            .authorizeHttpRequests(auth -> auth
                // Preflight do CORS
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Autenticação: /auth/me exige estar logado, o resto é público
                .antMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .antMatchers(HttpMethod.POST, "/auth/login", "/auth/logout", "/auth/cadastro").permitAll()

                // Eventos: leitura pública, criar exige login, editar/excluir só ADMIN
                .antMatchers(HttpMethod.GET, "/eventos/**").permitAll()
                .antMatchers(HttpMethod.POST, "/eventos/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/eventos/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/eventos/**").hasRole("ADMIN")

                // Participantes: lista e contagem do evento para quem está logado (emails mascarados para USER);
                // lista geral, busca por id e Excel (dados pessoais completos) só ADMIN
                .antMatchers(HttpMethod.GET, "/participantes/evento/**", "/participantes/quantidade/**").authenticated()
                .antMatchers(HttpMethod.GET, "/participantes", "/participantes/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/participantes/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/participantes/**").hasRole("ADMIN")

                // Lista de espera
                .antMatchers(HttpMethod.POST, "/lista-espera/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/lista-espera/**").hasRole("ADMIN")

                // Qualquer outra rota exige login
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escreverProblema(HttpServletResponse response, int status, String titulo) throws IOException {
        Problema problema = new Problema();
        problema.setStatus(status);
        problema.setDataHora(OffsetDateTime.now());
        problema.setTitulo(titulo);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), problema);
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}
