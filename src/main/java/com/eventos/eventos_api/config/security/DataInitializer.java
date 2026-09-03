package com.eventos.eventos_api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.domain.repository.UsuarioRepository;


@Component
public class DataInitializer implements CommandLineRunner{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!usuarioRepository.findByUsername("admin").isPresent()) {

            Usuario usuario = new Usuario();

            usuario.setUsername("admin");
            usuario.setSenha(passwordEncoder.encode("123456"));
            usuario.setRole("ADMIN");

            usuarioRepository.save(usuario);

            System.out.println("Usuário admin criado!");
        }
    }
}
