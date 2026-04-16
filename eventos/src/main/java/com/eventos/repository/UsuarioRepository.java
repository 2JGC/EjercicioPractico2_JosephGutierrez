package com.eventos.repository;

import com.eventos.domain.Rol;
import com.eventos.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //buscar por email
    Optional<Usuario> findByEmail(String email);

    //buscar usuarios por rol
    List<Usuario> findByRol(Rol rol);

    //buscar usuarios activos
    @Query("SELECT u FROM Usuario u WHERE u.activo = true")
    List<Usuario> findByActivoTrue();

    boolean existsByEmail(String email);
}
