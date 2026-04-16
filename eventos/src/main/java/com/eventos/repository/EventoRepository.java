package com.eventos.repository;

import com.eventos.domain.Evento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    //buscar eventos por estado activo/inactivo
    List<Evento> findByActivo(boolean activo);

    // buscar por coincidencia parcial en nombre
    List<Evento> findByNombreContainingIgnoreCase(String nombre);

    //eventos en rango de fechas
    @Query("SELECT e FROM Evento e WHERE e.fecha BETWEEN :inicio AND :fin")
    List<Evento> buscarPorRangoFechas(@Param("inicio") LocalDate inicio,
                                      @Param("fin") LocalDate fin);

    //contar eventos activos
    @Query(value = "SELECT COUNT(*) FROM evento WHERE activo = true", nativeQuery = true)
    long contarEventosActivos();
}
