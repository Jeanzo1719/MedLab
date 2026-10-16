package com.medlab.main.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Calificacion;

/**
 * Qué hace: da acceso a la tabla calificaciones y cuenta cuántas calificaciones tiene cada
 * reporte de disponibilidad y cuántas son favorables.
 *
 * Cómo entenderlo: es como un encuestador que no te entrega cada reseña, sino que te dice
 * "este reporte tiene 5 reseñas y 4 son favorables".
 *
 * Detalles técnicos: JpaRepository ya incluye, sin escribir código, estos métodos básicos:
 * - findAll(): todas las calificaciones (SELECT * FROM calificaciones).
 * - findById(id): una calificación por su id. Devuelve un Optional porque puede no existir.
 * - count(): cuántas calificaciones hay en total.
 * - existsById(id): true si existe una calificación con ese id.
 * Además define contarPorDisponibilidad, que MedicamentoDetalleService llama una sola vez
 * para todas las farmacias de un medicamento.
 */
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

  /**
   * Resultado del conteo de un reporte: su id, cuántas calificaciones tiene en total y
   * cuántas son del tipo favorable.
   */
  interface ConteoCalificaciones {

    /** Id del reporte de disponibilidad al que pertenece el conteo. */
    Integer getDisponibilidadId();

    /** Total de calificaciones del reporte. */
    Long getTotal();

    /** Cuántas de esas calificaciones son del tipo favorable. */
    Long getFavorables();
  }

  /**
   * Cuenta las calificaciones de varios reportes en una sola consulta.
   *
   * ids: ids de los reportes de disponibilidad a contar.
   * tipoFavorable: nombre del tipo de calificación que cuenta a favor, por ejemplo "Si hay".
   *
   * Devuelve una fila por cada reporte que tenga al menos una calificación; los reportes
   * sin calificaciones no aparecen en el resultado. Filtra por disponibilidad_id, la primera
   * columna del índice idx_calificaciones_disponibilidad_fecha.
   */
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