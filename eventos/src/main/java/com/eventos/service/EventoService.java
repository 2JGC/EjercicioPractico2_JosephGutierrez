package com.eventos.service;

import com.eventos.domain.Evento;
import com.eventos.repository.EventoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<Evento> getTodosLosEventos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Evento> getEvento(Long idEvento) {
        return eventoRepository.findById(idEvento);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarPorEstado(boolean activo) {
        return eventoRepository.findByActivo(activo);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarPorNombre(String nombre) {
        return eventoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return eventoRepository.buscarPorRangoFechas(inicio, fin);
    }

    @Transactional(readOnly = true)
    public long contarActivos() {
        return eventoRepository.contarEventosActivos();
    }

    @Transactional
    public void guardar(Evento evento) {
        eventoRepository.save(evento);
    }

    @Transactional
    public void eliminar(Long idEvento) {
        if (!eventoRepository.existsById(idEvento)) {
            throw new IllegalArgumentException("El evento con ID " + idEvento + " no existe.");
        }
        try {
            eventoRepository.deleteById(idEvento);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el evento.", e);
        }
    }
}
