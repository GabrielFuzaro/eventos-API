package com.eventos.eventos_api.config.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService { //Gera e valida o JWT e monta o cookie que o carrega

    public static final String NOME_COOKIE = "eventos_token";
    private static final String EMISSOR = "eventos-api";

    private final Key key;
    private final Duration validade;
    private final boolean cookieSeguro;

    public TokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiracao-minutos:60}") long expiracaoMinutos,
            @Value("${jwt.cookie-seguro:false}") boolean cookieSeguro) {

        //HS256 exige chave de pelo menos 256 bits: falha logo na subida com mensagem clara
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("A variável de ambiente JWT_SECRET precisa ter pelo menos 32 caracteres.");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validade = Duration.ofMinutes(expiracaoMinutos);
        this.cookieSeguro = cookieSeguro;
    }

    public String gerarToken(String username, String role) {
        long agora = System.currentTimeMillis();

        return Jwts.builder()
            .setIssuer(EMISSOR)
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date(agora))
            .setExpiration(new Date(agora + validade.toMillis()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /** Valida assinatura, expiração e emissor. Retorna o username ou null se o token não for aceito. */
    public String validarEExtrairUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(EMISSOR)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            //Token expirado, assinatura inválida, formato errado...: não deve ser aceito
            return null;
        }
    }

    /** Lê o token do cookie HttpOnly; aceita também "Authorization: Bearer" para ferramentas como Postman. */
    public String extrairToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (NOME_COOKIE.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    //HttpOnly: o JavaScript do front não consegue ler o token (protege contra roubo por XSS).
    //SameSite=Strict: o navegador não envia o cookie em requisições vindas de outros sites (proteção contra CSRF).
    //Secure: só trafega em HTTPS; ligue com JWT_COOKIE_SEGURO=true em produção.
    public ResponseCookie criarCookie(String token) {
        return montarCookie(token, validade);
    }

    public ResponseCookie limparCookie() {
        return montarCookie("", Duration.ZERO);
    }

    private ResponseCookie montarCookie(String valor, Duration maxAge) {
        return ResponseCookie.from(NOME_COOKIE, valor)
            .httpOnly(true)
            .secure(cookieSeguro)
            .sameSite("Strict")
            .path("/")
            .maxAge(maxAge)
            .build();
    }
}
