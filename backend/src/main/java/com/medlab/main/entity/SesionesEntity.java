package com.medlab.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SesionesEntity - mapea la tabla sesiones (HU-2).
 *
 * Cada inicio de sesión exitoso crea una fila con su JWT, la fecha en que empezó y la fecha en
 * que vence (14 días después). Mientras la sesión siga activa, el filtro de seguridad la acepta;
 * al cerrar sesión queda activa = FALSE y con fecha_cierre, y ese token ya no sirve.
 *
 * La ultima_actividad no se toca desde Java: la actualiza la base (ON UPDATE CURRENT_TIMESTAMP)
 * cada vez que el filtro registra actividad en la sesión.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sesiones")
public class SesionesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private UsuariosEntity usuario;

  /** El JWT firmado. Es UNIQUE: dos sesiones no pueden compartir token. */
  @Column(name = "token", nullable = false, length = 500)
  private String token;

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDateTime fechaInicio;

  /** La maneja la base con DEFAULT y ON UPDATE; Java solo la lee. */
  @Column(name = "ultima_actividad", nullable = false, insertable = false)
  private LocalDateTime ultimaActividad;

  @Column(name = "fecha_expiracion", nullable = false)
  private LocalDateTime fechaExpiracion;

  @Column(name = "fecha_cierre")
  private LocalDateTime fechaCierre;

  @Column(name = "activa", nullable = false)
  private boolean activa;
}
