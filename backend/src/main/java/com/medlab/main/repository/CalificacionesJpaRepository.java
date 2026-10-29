package com.medlab.main.repository;

import com.medlab.main.entity.CalificacionEntity;
import com.medlab.main.repository.CalificacionesRepository.CalificacionReciente;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CalificacionesJpaRepository extends JpaRepository<CalificacionEntity, Long> {

    /**
     * Trae, en una sola consulta, las calificaciones mas recientes de varios reportes.
     *
     * Por cada reporte numera sus calificaciones de la mas nueva a la mas antigua (por
     * fecha_hora y, si coinciden, por id) y deja solo las primeras "limite". Los reportes sin
     * calificaciones no aparecen en el resultado. Filtra por disponibilidad_id, la primera
     * columna del indice idx_calificaciones_disponibilidad_fecha.
     *
     * Es una consulta nativa porque JPQL no permite limitar filas por grupo (window function
     * row_number), por lo que no puede ser una consulta derivada.
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
    List<CalificacionReciente> findCalificacionesRecientesByDisponibilidad(
            @Param("ids") Collection<Integer> disponibilidadIds, @Param("limite") int limite);

    /**
     * Guarda la calificación de un usuario sobre un reporte de disponibilidad.
     *
     * Si el usuario todavía no había calificado ese reporte, crea la calificación. Si ya lo
     * había hecho, no crea otra: cambia el tipo y pone la fecha_hora de ahora, así su opinión
     * nueva cuenta entre las más recientes. Lo sabe por la restricción UNIQUE
     * (disponibilidad_id, usuario_id) de la tabla. Devuelve cuántas filas cambió la base de
     * datos. Es una consulta nativa porque JPQL no tiene "on duplicate key update"; por eso
     * lleva {@code @Modifying} y {@code @Transactional}.
     */
    @Transactional
    @Modifying
    @Query(value = """
            insert into calificaciones (disponibilidad_id, usuario_id, tipo_calificacion_id)
            values (:disponibilidadId, :usuarioId, :tipoCalificacionId)
            on duplicate key update tipo_calificacion_id = values(tipo_calificacion_id),
                                    fecha_hora = now()
            """, nativeQuery = true)
    int upsertCalificacion(@Param("disponibilidadId") Integer disponibilidadId,
            @Param("usuarioId") Integer usuarioId,
            @Param("tipoCalificacionId") Integer tipoCalificacionId);
}
