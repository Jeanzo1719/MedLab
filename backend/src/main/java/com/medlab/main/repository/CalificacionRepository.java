package com.medlab.main.repository;

import com.medlab.main.entity.Calificacion;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

  /** Conteo de calificaciones de una disponibilidad (una fila por disponibilidad que tenga calificaciones). */
  interface ConteoCalificaciones {
    Integer getDisponibilidadId();

    Long getTotal();

    Long getFavorables();
  }

  @Query("""
      select c.disponibilidad.id as disponibilidadId,
             count(c) as total,
             sum(case when c.tipoCalificacion.nombre = :tipoFavorable then 1 else 0 end) as favorables
      from Calificacion c
      where c.disponibilidad.id in :ids
      group by c.disponibilidad.id
      """)
  List<ConteoCalificaciones> contarPorDisponibilidad(
      @Param("ids") Collection<Integer> ids,
      @Param("tipoFavorable") String tipoFavorable);
}