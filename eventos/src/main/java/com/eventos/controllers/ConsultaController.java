package com.eventos.controllers;

import com.eventos.domain.Rol;
import com.eventos.service.EventoService;
import com.eventos.service.RolService;
import com.eventos.service.UsuarioService;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final RolService rolService;

    public ConsultaController(EventoService eventoService,
            UsuarioService usuarioService,
            RolService rolService) {
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("totalActivos", eventoService.contarActivos());
        return "/consultas/listado";
    }

    //eventos por estado
    @PostMapping("/porEstado")
    public String porEstado(@RequestParam boolean activo, Model model) {
        model.addAttribute("resultados", eventoService.buscarPorEstado(activo));
        model.addAttribute("tipoConsulta", "Eventos por estado: " + (activo ? "Activos" : "Inactivos"));
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("totalActivos", eventoService.contarActivos());
        return "/consultas/listado";
    }

    //eventos por rango de fechas
    @PostMapping("/porFechas")
    public String porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {
        model.addAttribute("resultados", eventoService.buscarPorRangoFechas(inicio, fin));
        model.addAttribute("tipoConsulta", "Eventos entre " + inicio + " y " + fin);
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("totalActivos", eventoService.contarActivos());
        return "/consultas/listado";
    }

    //usuarios por rol
    @PostMapping("/usuariosPorRol")
    public String usuariosPorRol(@RequestParam Long idRol, Model model) {
        Optional<Rol> rolOpt = rolService.getRol(idRol);
        if (rolOpt.isPresent()) {
            model.addAttribute("usuariosResultado", usuarioService.getTodosLosUsuarios().stream()
                    .filter(u -> u.getRol() != null && u.getRol().getIdRol().equals(idRol))
                    .toList());
            model.addAttribute("tipoConsulta", "Usuarios con rol: " + rolOpt.get().getNombre());
        }
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("totalActivos", eventoService.contarActivos());
        return "/consultas/listado";
    }

    //eventos por nombre
    @PostMapping("/porNombre")
    public String porNombre(@RequestParam String nombre, Model model) {
        model.addAttribute("resultados", eventoService.buscarPorNombre(nombre));
        model.addAttribute("tipoConsulta", "Eventos con nombre: \"" + nombre + "\"");
        model.addAttribute("roles", rolService.getTodosLosRoles());
        model.addAttribute("totalActivos", eventoService.contarActivos());
        return "/consultas/listado";
    }
}
