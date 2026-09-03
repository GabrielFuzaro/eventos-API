package com.eventos.eventos_api.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eventos.eventos_api.domain.model.Usuario;
import com.eventos.eventos_api.domain.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario Não Encontrado"));

        return User.builder()
        .username(usuario.getUsername())
        .password(usuario.getSenha())
        .roles(usuario.getRole())
        .build();
    }
}
