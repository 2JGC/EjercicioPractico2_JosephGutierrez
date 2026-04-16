package com.eventos.service;

import com.eventos.domain.Rol;
import com.eventos.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> getTodosLosRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRol(Long idRol) {
        return rolRepository.findById(idRol);
    }

    @Transactional
    public void guardar(Rol rol) {
        rolRepository.save(rol);
    }

    @Transactional
    public void eliminar(Long idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new IllegalArgumentException("El rol con ID " + idRol + " no existe.");
        }
        try {
            rolRepository.deleteById(idRol);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el rol. Tiene usuarios asociados.", e);
        }
    }
}
