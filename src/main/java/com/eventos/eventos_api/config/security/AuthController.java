package com.eventos.eventos_api.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eventos.eventos_api.assembler.UsuarioAssembler;
import com.eventos.eventos_api.domain.exception.NegocioException;
import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.domain.repository.UsuarioRepository;
import com.eventos.eventos_api.model.output.UsuarioOutput;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;
    private UsuarioAssembler usuarioAssembler;
    private LoginTentativaService loginTentativaService;

    @PostMapping("/login") //Autentica e devolve o token num cookie HttpOnly; o corpo traz só os dados do usuário
    public ResponseEntity<UsuarioOutput> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        loginTentativaService.verificarBloqueio(request.getUsername(), ip);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e) {
            loginTentativaService.registrarFalha(request.getUsername(), ip);
            throw e; //ApiExceptionHandler responde 401 com mensagem genérica (não revela se o usuário existe)
        }
        loginTentativaService.registrarSucesso(request.getUsername(), ip);

        Usuario usuario = buscarUsuario(authentication.getName());
        String token = tokenService.gerarToken(usuario.getUsername(), usuario.getRole());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, tokenService.criarCookie(token).toString())
            .body(usuarioAssembler.toOutput(usuario));
    }

    @PostMapping("/logout") //Apaga o cookie do token
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, tokenService.limparCookie().toString())
            .build();
    }

    @GetMapping("/me") //Usuário logado (o front não consegue ler o cookie HttpOnly, então pergunta aqui)
    public UsuarioOutput usuarioLogado(Authentication authentication) {
        return usuarioAssembler.toOutput(buscarUsuario(authentication.getName()));
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioOutput cadastro(@Valid @RequestBody CadastroRequest request) {

        String username = request.getUsername().trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new NegocioException("Já existe um usuário com esse nome");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole("USER"); //Cadastro público sempre cria USER, nunca ADMIN

        return usuarioAssembler.toOutput(usuarioRepository.save(usuario));
    }

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new NegocioException("Usuário não encontrado"));
    }
}
