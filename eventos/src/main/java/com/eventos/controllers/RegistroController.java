package com.eventos.controllers;

import com.eventos.domain.Rol;
import com.eventos.domain.Usuario;
import com.eventos.repository.RolRepository;
import com.eventos.repository.UsuarioRepository;
import com.eventos.service.NotificacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final NotificacionService notificacionService;

    public RegistroController(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            NotificacionService notificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuarioNuevo", new Usuario());
        return "/registro/nuevo";
    }

    @PostMapping("/guardar")
    public String registrar(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {
        // Verificar si el correo ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("error",
                    "El correo ya está registrado. Usa otro correo.");
            return "redirect:/registro/nuevo";
        }

        // Verificar campos obligatorios
        if (usuario.getNombre() == null || usuario.getNombre().isBlank() ||
            usuario.getEmail() == null || usuario.getEmail().isBlank() ||
            usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute("error",
                    "Todos los campos son obligatorios.");
            return "redirect:/registro/nuevo";
        }

        // Asignar rol cliente por defecto
        Optional<Rol> rolCliente = rolRepository.findByNombre("CLIENTE");
        if (rolCliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Error interno: rol CLIENTE no encontrado.");
            return "redirect:/registro/nuevo";
        }

        usuario.setRol(rolCliente.get());
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // Enviar correo de bienvenida al nuevo usuario registrado
        try {
            notificacionService.enviarBienvenida(usuario.getEmail(), usuario.getNombre());
        } catch (Exception e) {
            System.err.println("Error al enviar correo de bienvenida: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("todoOk",
                "¡Registro exitoso! Te enviamos un correo de bienvenida.");
        return "redirect:/login";
    }
}
