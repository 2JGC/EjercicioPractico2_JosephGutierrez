package com.eventos.controllers;

import com.eventos.domain.Usuario;
import com.eventos.service.RolService;
import com.eventos.service.UsuarioService;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("usuarios", usuarioService.getTodosLosUsuarios());
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("usuarioNuevo", new Usuario());
        return "/usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
            @RequestParam(required = false) Long rolId,
            @RequestParam(required = false) String activo,
            RedirectAttributes redirectAttributes) {
        try {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            usuario.setActivo("true".equals(activo));
            if (rolId != null) {
                rolService.getRol(rolId).ifPresent(usuario::setRol);
            }
            usuarioService.guardar(usuario, esNuevo);
            redirectAttributes.addFlashAttribute("todoOk",
                    esNuevo ? "Usuario creado correctamente." : "Usuario actualizado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Long idUsuario, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario/listado";
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setPassword("");
        model.addAttribute("usuarioNuevo", usuario);
        model.addAttribute("usuarios", usuarioService.getTodosLosUsuarios());
        model.addAttribute("roles", rolService.getTodosLosRoles());
        return "/usuario/listado";
    }

    @GetMapping("/detalle/{idUsuario}")
    public String detalle(@PathVariable Long idUsuario, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        return "/usuario/detalle";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(idUsuario);
            redirectAttributes.addFlashAttribute("todoOk", "Usuario eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }
}
