package com.medlab.main.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.FarmaciasEntity;

/**
 * Consultas de solo lectura del panel administrador (HU-9). Son nativas porque cruzan tablas
 * sin entity (usuarios, roles, pacientes) que HU-1/HU-2 van a crear; así no hay choque con
 * ellas. Los filtros opcionales llegan como texto vacío (''), no como null, para no depender
 * de cómo cada driver tipa un parámetro nulo.
 */
public interface HistorialAdminRepository extends Repository<FarmaciasEntity, Integer> {

  interface UsuarioHistorial {
    Integer getId();

    String getIdentificacion();

    String getRol();

    String getNombre();

    LocalDateTime getFechaRegistro();
  }

  interface FarmaciaHistorial {
    Integer getId();

    String getNombre();

    String getEstado();

    LocalDateTime getFechaUltimaActualizacion();

    Long getAlertasPendientes();
  }

  interface FarmaciaConAlertas {
    Integer getId();

    String getNombre();

    String getEstado();

    Long getTotalAlertas();

    Long getAlertasPendientes();

    LocalDateTime getUltimaAlerta();
  }

  interface DisponibilidadAdmin {
    Integer getId();

    String getMedicamento();

    String getPresentacion();

    String getEstado();

    Integer getCantidad();

    LocalDateTime getFechaHoraReporte();

    Long getTotalCalificaciones();
  }

  interface CalificacionAdmin {
    Long getId();

    Integer getUsuarioId();

    String getIdentificacion();

    String getTipo();

    LocalDateTime getFechaHora();
  }

  /** Usuarios con el nombre de su perfil: paciente o farmacia (el administrador no tiene). */
  @Query(value = """
      SELECT u.id AS id, u.identificacion AS identificacion, r.nombre AS rol,
             COALESCE(CONCAT(p.primer_nombre, ' ', p.apellidos), f.nombre_farmacia) AS nombre,
             u.fecha_registro AS fechaRegistro
      FROM usuarios u
      JOIN roles r ON r.id = u.rol_id
      LEFT JOIN pacientes p ON p.usuario_id = u.id
      LEFT JOIN farmacias f ON f.usuario_id = u.id
      WHERE (:rol = '' OR r.nombre = :rol)
      ORDER BY u.fecha_registro DESC, u.id DESC
      """, countQuery = """
      SELECT COUNT(*) FROM usuarios u JOIN roles r ON r.id = u.rol_id
      WHERE (:rol = '' OR r.nombre = :rol)
      """, nativeQuery = true)
  Page<UsuarioHistorial> listarUsuarios(@Param("rol") String rol, Pageable paginacion);

  /** Farmacias con su estado, su última actualización y cuántas alertas tiene sin atender. */
  @Query(value = """
      SELECT f.usuario_id AS id, f.nombre_farmacia AS nombre, ef.nombre AS estado,
             f.fecha_ultima_actualizacion AS fechaUltimaActualizacion,
             (SELECT COUNT(*) FROM alertas_inactividad_farmacia a
               WHERE a.farmacia_id = f.usuario_id AND a.atendida = FALSE) AS alertasPendientes
      FROM farmacias f
      JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
      WHERE (:estado = '' OR ef.nombre = :estado)
      ORDER BY f.fecha_ultima_actualizacion DESC, f.usuario_id
      """, countQuery = """
      SELECT COUNT(*) FROM farmacias f JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
      WHERE (:estado = '' OR ef.nombre = :estado)
      """, nativeQuery = true)
  Page<FarmaciaHistorial> listarFarmacias(@Param("estado") String estado, Pageable paginacion);

  /** Solo farmacias que han tenido alguna alerta; primero las que tienen más sin atender. */
  @Query(value = """
      SELECT f.usuario_id AS id, f.nombre_farmacia AS nombre, ef.nombre AS estado,
             COUNT(a.id) AS totalAlertas,
             SUM(CASE WHEN a.atendida = FALSE THEN 1 ELSE 0 END) AS alertasPendientes,
             MAX(a.fecha_generada) AS ultimaAlerta
      FROM farmacias f
      JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
      JOIN alertas_inactividad_farmacia a ON a.farmacia_id = f.usuario_id
      GROUP BY f.usuario_id, f.nombre_farmacia, ef.nombre
      ORDER BY alertasPendientes DESC, ultimaAlerta DESC
      """, countQuery = """
      SELECT COUNT(DISTINCT a.farmacia_id) FROM alertas_inactividad_farmacia a
      """, nativeQuery = true)
  Page<FarmaciaConAlertas> listarFarmaciasConAlertas(Pageable paginacion);

  /** Publicaciones de una farmacia, con cuántas calificaciones tiene cada una. */
  @Query(value = """
      SELECT d.id AS id, m.nombre_comercial AS medicamento, m.presentacion AS presentacion,
             ed.nombre AS estado, d.cantidad_disponible AS cantidad,
             d.fecha_hora_reporte AS fechaHoraReporte,
             (SELECT COUNT(*) FROM calificaciones c WHERE c.disponibilidad_id = d.id)
               AS totalCalificaciones
      FROM disponibilidad d
      JOIN medicamentos m ON m.id = d.medicamento_id
      JOIN estados_disponibilidad ed ON ed.id = d.estado_disponibilidad_id
      WHERE d.farmacia_id = :farmaciaId
      ORDER BY m.nombre_comercial, d.id
      """, countQuery = """
      SELECT COUNT(*) FROM disponibilidad d WHERE d.farmacia_id = :farmaciaId
      """, nativeQuery = true)
  Page<DisponibilidadAdmin> listarDisponibilidades(@Param("farmaciaId") Integer farmaciaId,
      Pageable paginacion);

  /** Calificaciones de una publicación con quién las hizo; la entity no mapea usuario_id. */
  @Query(value = """
      SELECT c.id AS id, c.usuario_id AS usuarioId, u.identificacion AS identificacion,
             t.nombre AS tipo, c.fecha_hora AS fechaHora
      FROM calificaciones c
      JOIN tipos_calificacion t ON t.id = c.tipo_calificacion_id
      JOIN usuarios u ON u.id = c.usuario_id
      WHERE c.disponibilidad_id = :disponibilidadId
      ORDER BY c.fecha_hora DESC, c.id DESC
      """, countQuery = """
      SELECT COUNT(*) FROM calificaciones c WHERE c.disponibilidad_id = :disponibilidadId
      """, nativeQuery = true)
  Page<CalificacionAdmin> listarCalificaciones(@Param("disponibilidadId") Integer disponibilidadId,
      Pageable paginacion);

  /** Cuántos usuarios con ese id tienen ese rol (0 o 1); así se comprueba que es administrador. */
  @Query(value = """
      SELECT COUNT(*) FROM usuarios u JOIN roles r ON r.id = u.rol_id
      WHERE u.id = :usuarioId AND r.nombre = :rol
      """, nativeQuery = true)
  long contarConRol(@Param("usuarioId") Integer usuarioId, @Param("rol") String rol);

  interface DisponibilidadSospechosa {
    Integer getId();

    Integer getFarmaciaId();

    String getFarmacia();

    String getMedicamento();

    String getPresentacion();

    Long getCalificacionesNoHay();

    Long getCalificacionesRevisadas();
  }

  /**
   * Publicaciones con al menos "umbral" calificaciones del tipo dado entre las últimas "ventana".
   * disponibilidadId = 0 revisa todas; otro valor revisa solo esa. Numera las calificaciones de
   * cada publicación de la más nueva a la más vieja y cuenta solo las primeras.
   */
  @Query(value = """
      SELECT d.id AS id, d.farmacia_id AS farmaciaId, f.nombre_farmacia AS farmacia,
             m.nombre_comercial AS medicamento, m.presentacion AS presentacion,
             r.no_hay AS calificacionesNoHay, r.revisadas AS calificacionesRevisadas
      FROM (
        SELECT x.disponibilidad_id AS disponibilidad_id, COUNT(*) AS revisadas,
               SUM(CASE WHEN x.tipo = :tipoNoHay THEN 1 ELSE 0 END) AS no_hay
        FROM (
          SELECT c.disponibilidad_id AS disponibilidad_id, t.nombre AS tipo,
                 ROW_NUMBER() OVER (PARTITION BY c.disponibilidad_id
                                    ORDER BY c.fecha_hora DESC, c.id DESC) AS posicion
          FROM calificaciones c
          JOIN tipos_calificacion t ON t.id = c.tipo_calificacion_id
          WHERE (:disponibilidadId = 0 OR c.disponibilidad_id = :disponibilidadId)
        ) x
        WHERE x.posicion <= :ventana
        GROUP BY x.disponibilidad_id
      ) r
      JOIN disponibilidad d ON d.id = r.disponibilidad_id
      JOIN farmacias f ON f.usuario_id = d.farmacia_id
      JOIN medicamentos m ON m.id = d.medicamento_id
      WHERE r.no_hay >= :umbral
      ORDER BY r.no_hay DESC, d.id
      LIMIT :limite
      """, nativeQuery = true)
  List<DisponibilidadSospechosa> buscarSospechosas(@Param("disponibilidadId") int disponibilidadId,
      @Param("ventana") int ventana, @Param("umbral") int umbral,
      @Param("tipoNoHay") String tipoNoHay, @Param("limite") int limite);
}