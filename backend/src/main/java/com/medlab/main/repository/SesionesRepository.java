package com.medlab.main.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.medlab.main.entity.SesionesEntity;

/**
 * Sesiones de los usuarios (HU-2). Cada fila guarda el JWT, su vencimiento y si sigue activa.
 */
public interface SesionesRepository {

  /** Busca la sesión dueña de un token; si no existe, el token se rechaza. */
  Optional<SesionesEntity> findByToken(String token);

  /** Crea o actualiza una sesión. */
  SesionesEntity save(SesionesEntity sesionesEntity);

  /**
   * Cierra todas las sesiones abiertas de un usuario. Usa idx_sesiones_usuario_activa para
   * resolver si ya tenía una sesión vigente antes de crear la nueva.
   */
  void cerrarActivasDeUsuario(Integer usuarioId, LocalDateTime fechaCierre);

  /** Marca actividad en la sesión; la base actualiza ultima_actividad (ON UPDATE). */
  void registrarActividad(Integer sesionId);
}
