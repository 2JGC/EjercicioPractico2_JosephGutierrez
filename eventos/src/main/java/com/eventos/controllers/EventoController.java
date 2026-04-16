package com.eventos.controllers;

import com.eventos.domain.Evento;
import com.eventos.service.EventoService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/evento")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("eventos", eventoService.getTodosLosEventos());
        model.addAttribute("eventoNuevo", new Evento());
        return "/evento/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Evento evento,
            @RequestParam(required = false) String activo,
            RedirectAttributes redirectAttributes) {
        try {
            // Checkbox: si no está marcado no se envía (viene null = false)
            evento.setActivo("true".equals(activo));
            eventoService.guardar(evento);
            redirectAttributes.addFlashAttribute("todoOk", "Evento guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el evento: " + e.getMessage());
        }
        return "redirect:/evento/listado";
    }

    @GetMapping("/modificar/{idEvento}")
    public String modificar(@PathVariable Long idEvento, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Evento> eventoOpt = eventoService.getEvento(idEvento);
        if (eventoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Evento no encontrado.");
            return "redirect:/evento/listado";
        }
        model.addAttribute("eventoNuevo", eventoOpt.get());
        model.addAttribute("eventos", eventoService.getTodosLosEventos());
        return "/evento/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idEvento, RedirectAttributes redirectAttributes) {
        try {
            eventoService.eliminar(idEvento);
            redirectAttributes.addFlashAttribute("todoOk", "Evento eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/evento/listado";
    }
}
