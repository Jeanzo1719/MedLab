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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Historial de cambios de estado de una farmacia (aprobada, suspendida...).
 *
 * usuarioAdminId es un Integer y no una relación porque aún no existe la entity Usuario (la
 * crean HU-1/HU-2). Es null cuando el cambio lo hizo el sistema, como la suspensión automática
 * por inactividad. Es un registro: no se modifica después de crearlo.
 */
@Entity
@Table(name = "auditoria_estados_farmacia")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditoriaEstadoFarmacia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private Farmacia farmacia;

  @Column(name = "usuario_admin_id")
  private Integer usuarioAdminId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_anterior_id", nullable = false)
  private EstadoFarmacia estadoAnterior;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_nuevo_id", nullable = false)
  private EstadoFarmacia estadoNuevo;

  @Column(nullable = false)
  private LocalDateTime fecha;

  public AuditoriaEstadoFarmacia(Farmacia farmacia, Integer usuarioAdminId,
      EstadoFarmacia estadoAnterior, EstadoFarmacia estadoNuevo, LocalDateTime fecha) {
    this.farmacia = farmacia;
    this.usuarioAdminId = usuarioAdminId;
    this.estadoAnterior = estadoAnterior;
    this.estadoNuevo = estadoNuevo;
    this.fecha = fecha;
  }
}