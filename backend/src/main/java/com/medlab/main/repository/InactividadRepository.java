package com.medlab.main.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Farmacia;

/**
 * Consultas nativas de la inactividad de farmacias (HU-9). Son nativas para escribir alertas,
 * estado y auditoría sin tocar las entities compartidas, y para que cada paso del job sea una
 * sola sentencia SQL. Las fechas llegan siempre desde Java, no de la BD.
 */
public interface InactividadRepository extends Repository<Farmacia, Integer> {

  /**
   * Farmacias aprobadas sin actualizar desde antes del límite y sin ninguna alerta posterior a
   * él. Ese NOT EXISTS evita generar una alerta por día mientras siguen inactivas.
   */
  @Query(value = """
      SELECT f.usuario_id
      FROM farmacias f
      JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
      WHERE ef.nombre = 'aprobada'
        AND f.fecha_ultima_actualizacion < :limite
        AND NOT EXISTS (
          SELECT 1 FROM alertas_inactividad_farmacia a
          WHERE a.farmacia_id = f.usuario_id AND a.fecha_generada >= :limite)
      ORDER BY f.usuario_id
      """, nativeQuery = true)
  List<Integer> farmaciasParaAlertar(@Param("limite") LocalDateTime limite);

  @Modifying
  @Query(value = """
      INSERT INTO alertas_inactividad_farmacia (farmacia_id, fecha_generada, atendida)
      VALUES (:farmaciaId, :fecha, FALSE)
      """, nativeQuery = true)
  int crearAlerta(@Param("farmaciaId") Integer farmaciaId, @Param("fecha") LocalDateTime fecha);

  @Query(value = """
      SELECT COUNT(*) FROM alertas_inactividad_farmacia
      WHERE farmacia_id = :farmaciaId AND atendida = FALSE
      """, nativeQuery = true)
  long contarPendientes(@Param("farmaciaId") Integer farmaciaId);

  /** Solo pasa a suspendida si sigue aprobada; devuelve 0 si otro proceso ya la cambió. */
  @Modifying
  @Query(value = """
      UPDATE farmacias
      SET estado_aprobacion_id = (SELECT id FROM estados_farmacia WHERE nombre = 'suspendida')
      WHERE usuario_id = :farmaciaId
        AND estado_aprobacion_id = (SELECT id FROM estados_farmacia WHERE nombre = 'aprobada')
      """, nativeQuery = true)
  int suspender(@Param("farmaciaId") Integer farmaciaId);

  /** Suspensión automática: sin administrador, por eso usuario_admin_id va en NULL. */
  @Modifying
  @Query(value = """
      INSERT INTO auditoria_estados_farmacia
        (farmacia_id, usuario_admin_id, estado_anterior_id, estado_nuevo_id, fecha)
      SELECT :farmaciaId, NULL, ant.id, nue.id, :fecha
      FROM estados_farmacia ant, estados_farmacia nue
      WHERE ant.nombre = 'aprobada' AND nue.nombre = 'suspendida'
      """, nativeQuery = true)
  int registrarAuditoriaSuspension(@Param("farmaciaId") Integer farmaciaId,
      @Param("fecha") LocalDateTime fecha);

  @Modifying
  @Query(value = """
      UPDATE alertas_inactividad_farmacia SET atendida = TRUE
      WHERE farmacia_id = :farmaciaId AND atendida = FALSE
      """, nativeQuery = true)
  int marcarAlertasAtendidas(@Param("farmaciaId") Integer farmaciaId);

  @Modifying
  @Query(value = """
      UPDATE farmacias SET fecha_ultima_actualizacion = :fecha WHERE usuario_id = :farmaciaId
      """, nativeQuery = true)
  int moverFechaActualizacion(@Param("farmaciaId") Integer farmaciaId,
      @Param("fecha") LocalDateTime fecha);
}