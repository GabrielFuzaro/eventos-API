package com.eventos.eventos_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {
    
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails usuario = User.builder()
            .username("admin")
            .password(encoder.encode("senha123")) // a senha nunca fica em texto puro, sempre passa pelo encoder
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(usuario);
    }
}
