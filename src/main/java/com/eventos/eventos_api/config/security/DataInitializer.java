package com.eventos.eventos_api.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.domain.repository.UsuarioRepository;


@Component
public class DataInitializer implements CommandLineRunner{ //Cria o usuário administrador inicial, se ainda não existir

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String SENHA_PADRAO_DEV = "123456";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.senha:" + SENHA_PADRAO_DEV + "}")
    private String adminSenha;

    @Override
    public void run(String... args) throws Exception {

        if (!usuarioRepository.findByUsername(adminUsername).isPresent()) {

            Usuario usuario = new Usuario();

            usuario.setUsername(adminUsername);
            usuario.setSenha(passwordEncoder.encode(adminSenha));
            usuario.setRole("ADMIN");

            usuarioRepository.save(usuario);

            if (SENHA_PADRAO_DEV.equals(adminSenha)) {
                log.warn("Usuário '{}' criado com a senha padrão de desenvolvimento. Defina ADMIN_SENHA fora do ambiente local.", adminUsername);
            }
        }
    }
}
