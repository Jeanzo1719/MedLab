package com.medlab.main.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.entity.SesionesEntity;

@Repository
public interface SesionesJpaRepository extends JpaRepository<SesionesEntity, Integer> {

  /** Trae al usuario y su rol en la misma consulta: el filtro los necesita en cada solicitud. */
  @EntityGraph(attributePaths = { "usuario", "usuario.rol" })
  Optional<SesionesEntity> findByToken(String token);

  /**
   * Las dos consultas de abajo son masivas a propósito: cerrar sesiones viejas y tocar la
   * actividad son una sola sentencia, y CURRENT_TIMESTAMP lo resuelve la base.
   */
  @Modifying
  @Transactional
  @Query("UPDATE SesionesEntity s SET s.activa = false, s.fechaCierre = :fechaCierre "
      + "WHERE s.usuario.id = :usuarioId AND s.activa = true")
  void cerrarActivasDeUsuario(@Param("usuarioId") Integer usuarioId,
      @Param("fechaCierre") LocalDateTime fechaCierre);

  @Modifying
  @Transactional
  @Query("UPDATE SesionesEntity s SET s.ultimaActividad = CURRENT_TIMESTAMP WHERE s.id = :sesionId")
  void registrarActividad(@Param("sesionId") Integer sesionId);
}
