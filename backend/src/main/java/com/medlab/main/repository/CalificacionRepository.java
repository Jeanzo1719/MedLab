package com.medlab.main.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.entity.Calificacion;

/**
 * Qué hace: da acceso a la tabla calificaciones y trae las calificaciones más recientes de
 * cada reporte de disponibilidad.
 *
 * Cómo entenderlo: es como pedirle a un encuestador "dame solo las últimas 5 reseñas de cada
 * tienda", en lugar de todas las reseñas de siempre.
 *
 * Detalles técnicos: JpaRepository ya incluye, sin escribir código, estos métodos básicos:
 * - findAll(): todas las calificaciones (SELECT * FROM calificaciones).
 * - findById(id): una calificación por su id. Devuelve un Optional porque puede no existir.
 * - count(): cuántas calificaciones hay en total.
 * - existsById(id): true si existe una calificación con ese id.
 * Además define buscarRecientesPorDisponibilidad, que MedicamentoDetalleService llama una
 * sola vez para todas las farmacias de un medicamento.
 */
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

  /**
   * Una calificación reciente: el id del reporte al que pertenece y el nombre de su tipo
   * ("Si hay", "Bajo pedido" o "No hay").
   */
  interface CalificacionReciente {

    /** Id del reporte de disponibilidad al que pertenece la calificación. */
    Integer getDisponibilidadId();

    /** Nombre del tipo de calificación, tal como está en tipos_calificacion. */
    String getTipo();
  }

  /**
   * Trae, en una sola consulta, las calificaciones más recientes de varios reportes.
   *
   * ids: ids de los reportes de disponibilidad.
   * limite: cuántas calificaciones recientes se traen por cada reporte.
   *
   * Por cada reporte numera sus calificaciones de la más nueva a la más antigua (por
   * fecha_hora y, si coinciden, por id) y deja solo las primeras "limite". Los reportes sin
   * calificaciones no aparecen en el resultado. Filtra por disponibilidad_id, la primera
   * columna del índice idx_calificaciones_disponibilidad_fecha. Es una consulta nativa
   * porque JPQL no permite limitar filas por grupo.
   */
  @Query(value = """
      select recientes.disponibilidad_id as disponibilidadId,
             recientes.tipo as tipo
      from (
        select c.disponibilidad_id as disponibilidad_id,
               t.nombre as tipo,
               row_number() over (partition by c.disponibilidad_id
                                  order by c.fecha_hora desc, c.id desc) as posicion
        from calificaciones c
        join tipos_calificacion t on t.id = c.tipo_calificacion_id
        where c.disponibilidad_id in (:ids)
      ) recientes
      where recientes.posicion <= :limite
      """, nativeQuery = true)
  List<CalificacionReciente> buscarRecientesPorDisponibilidad(
      @Param("ids") Collection<Integer> ids,
      @Param("limite") int limite);

  @Transactional
  @Modifying
  @Query(value = """
      insert into calificaciones (disponibilidad_id, usuario_id, tipo_calificacion_id)
      values (:disponibilidadId, :usuarioId, :tipoCalificacionId)
      on duplicate key update tipo_calificacion_id = values(tipo_calificacion_id),
                              fecha_hora = current_timestamp
      """, nativeQuery = true)
  int registrar(@Param("disponibilidadId") Integer disponibilidadId,
      @Param("usuarioId") Integer usuarioId,
      @Param("tipoCalificacionId") Integer tipoCalificacionId);
}