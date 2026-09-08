package com.eventos.eventos_api.config.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
    
    
    private final String secret;
    private final Key key; 

    public TokenService(@Value("${jwt.secret}") String secret){
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String gerarToken(String username, String role) {
        long agora = System.currentTimeMillis();
        long umaHoraEmMillis = 1000* 60 * 60;

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
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
