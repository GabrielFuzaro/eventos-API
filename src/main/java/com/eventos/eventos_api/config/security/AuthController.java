package com.eventos.eventos_api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        // Isso dispara a checagem de usuário/senha usando o UserDetailsService configurado.
        // Se a senha estiver errada, uma exceção é lançada automaticamente pelo Spring Security
        // (capturada pelo seu ApiExceptionHandler global, retornando 401/403)

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = tokenService.gerarToken(request.getUsername());
        return ResponseEntity.ok(token);
    }
}
