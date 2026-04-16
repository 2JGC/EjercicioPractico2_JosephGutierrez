package com.eventos.controllers;

import com.eventos.domain.Rol;
import com.eventos.service.RolService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rol")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("rolNuevo", new Rol());
        return "/rol/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Rol rol, RedirectAttributes redirectAttributes) {
        try {
            rolService.guardar(rol);
            redirectAttributes.addFlashAttribute("todoOk", "Rol guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el rol: " + e.getMessage());
        }
        return "redirect:/rol/listado";
    }

    @GetMapping("/modificar/{idRol}")
    public String modificar(@PathVariable Long idRol, Model model, RedirectAttributes redirectAttributes) {
        Optional<Rol> rolOpt = rolService.getRol(idRol);
        if (rolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado.");
            return "redirect:/rol/listado";
        }
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("rolNuevo", rolOpt.get());
        return "/rol/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idRol, RedirectAttributes redirectAttributes) {
        try {
            rolService.eliminar(idRol);
            redirectAttributes.addFlashAttribute("todoOk", "Rol eliminado correctamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rol/listado";
    }
}
