package com.eventos.eventos_api.config.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
    
    // Em produção, essa chave NUNCA deve ficar hardcoded no código —
    // deve vir de uma variável de ambiente ou do application.properties,
    // carregada de forma segura. Aqui está direto no código só pra fins didáticos.
    // Precisa ter pelo menos 32 caracteres (256 bits) para o algoritmo HS256.
    
    private final String SECRET = "chave-secreta-bem-longa-e-dificil-de-adivinhar-12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String gerarToken(String username) {
        long agora = System.currentTimeMillis();
        long umaHoraEmMillis = 1000* 60 * 60;

        return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(agora))
        .setExpiration(new Date(agora + umaHoraEmMillis))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

      public String extrairUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // qualquer exceção aqui (token expirado, assinatura inválida, formato errado)
            // significa que o token não deve ser aceito
            return false;
        }
    }
}
