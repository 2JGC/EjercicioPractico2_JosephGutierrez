package com.eventos.service;

import com.eventos.domain.Usuario;
import com.eventos.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service("userDetailsService")
public class AccesoDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AccesoDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("DEBUG LOGIN - Buscando email: " + email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("DEBUG LOGIN - Usuario NO encontrado: " + email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        System.out.println("DEBUG LOGIN - Usuario encontrado: " + usuario.getNombre());
        System.out.println("DEBUG LOGIN - Password en BD: " + usuario.getPassword());
        System.out.println("DEBUG LOGIN - Rol: " + usuario.getRol().getNombre());

        var autoridad = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre());

        return new User(usuario.getEmail(), usuario.getPassword(), Set.of(autoridad));
    }
}
