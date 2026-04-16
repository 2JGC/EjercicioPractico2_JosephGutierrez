package com.eventos.service;

import com.eventos.domain.Usuario;
import com.eventos.repository.RolRepository;
import com.eventos.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            NotificacionService notificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificacionService = notificacionService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public void guardar(Usuario usuario, boolean esNuevo) {
        if (esNuevo) {
            // Verificar gmail duplicado
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new DataIntegrityViolationException("El correo ya está registrado.");
            }
            // Guardar contra
            usuarioRepository.save(usuario);
            // Enviar correo de bienvenida
            try {
                notificacionService.enviarBienvenida(usuario.getEmail(), usuario.getNombre());
            } catch (MessagingException e) {
                System.err.println("Error al enviar correo: " + e.getMessage());
            }
        } else {
            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
            // Si la contraseña viene sin nda conservar la actual
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                // Guardar contra
                usuario.setPassword(usuario.getPassword());
            }
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void eliminar(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }
}
