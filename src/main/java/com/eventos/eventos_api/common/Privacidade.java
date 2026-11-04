package com.eventos.eventos_api.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class Privacidade { //Regras para não expor dados pessoais a quem não é ADMIN

    private Privacidade() {}

    public static boolean usuarioAtualEhAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /** "joao.silva@gmail.com" -> "jo*******@gmail.com" */
    public static String mascararEmail(String email) {
        if (email == null) {
            return null;
        }
        int arroba = email.indexOf('@');
        if (arroba <= 0) {
            return "***";
        }
        String usuario = email.substring(0, arroba);
        String visivel = usuario.length() <= 2 ? usuario.substring(0, 1) : usuario.substring(0, 2);
        StringBuilder mascara = new StringBuilder(visivel);
        for (int i = visivel.length(); i < Math.max(usuario.length(), 3); i++) {
            mascara.append('*');
        }
        return mascara + email.substring(arroba);
    }
}
