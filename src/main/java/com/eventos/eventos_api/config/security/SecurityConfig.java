package com.eventos.eventos_api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita proteção CSRF — explicado no porquê logo abaixo
            
            .csrf().disable()
           
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define quais rotas exigem autenticação e quais são livres
            .authorizeHttpRequests(auth -> auth
                // Toda requisição OPTIONS é liberada (necessário pro CORS funcionar — ver Parte 9)
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Leitura de eventos é pública, qualquer um pode ver
                .antMatchers(HttpMethod.GET, "/eventos/**").permitAll()

                // Criar, editar e excluir evento exige estar autenticado
                .antMatchers(HttpMethod.POST, "/eventos/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/eventos/**").authenticated()
                .antMatchers(HttpMethod.POST, "/participantes/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/participantes/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/eventos/**").hasRole("ADMIN")
                // O endpoint de login precisa ficar público (senão ninguém consegue logar)
                .antMatchers("/auth/**").permitAll()

                // Qualquer outra rota não listada acima: por padrão, deixamos pública
                // (você pode trocar para .authenticated() se preferir bloquear tudo que não foi listado)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // Ativa autenticação via usuário/senha no header (Basic Auth) — usado só na Parte 4,
            // depois trocado por JWT na Parte 6 em diante
            .httpBasic();

            http.cors();

        return http.build();
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
