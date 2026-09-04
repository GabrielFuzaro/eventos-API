package com.eventos.eventos_api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventos.eventos_api.domain.exception.NegocioException;
import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.domain.repository.UsuarioRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        // Isso dispara a checagem de usuário/senha usando o UserDetailsService configurado.
        // Se a senha estiver errada, uma exceção é lançada automaticamente pelo Spring Security
        // (capturada pelo seu ApiExceptionHandler global, retornando 401/403)

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String role = authentication.getAuthorities()
            .iterator()
            .next()
            .getAuthority()
            .replace("ROLE_", "");

        String token = tokenService.gerarToken(request.getUsername(), role);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/cadastro")
    public Usuario cadastro(@RequestBody CadastroRequest request) {

       if(usuarioRepository.existsByUsername(request.getUsername())){
        throw new NegocioException("Já existe um usuário com esse nome");
       }

       Usuario usuario = new Usuario();
       usuario.setUsername(request.getUsername());
       usuario.setSenha(passwordEncoder.encode(request.getSenha()));
       usuario.setRole("USER");

       return usuarioRepository.save(usuario);
    }
}
