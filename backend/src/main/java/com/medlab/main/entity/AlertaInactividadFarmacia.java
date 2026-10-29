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
 * Aviso de que una farmacia lleva más de 30 días sin actualizarse (HU-9).
 *
 * La fecha se pone desde Java y no con el DEFAULT de la BD: así toda la lógica de inactividad
 * compara fechas del mismo reloj (el del servidor). "atendida" no tiene setter: solo cambia con
 * la consulta masiva del repositorio, para que marcar todas las alertas de una farmacia sea una
 * sola operación.
 */
@Entity
@Table(name = "alertas_inactividad_farmacia")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertaInactividadFarmacia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private FarmaciasEntity farmacia;

  @Column(name = "fecha_generada", nullable = false)
  private LocalDateTime fechaGenerada;

  @Column(nullable = false)
  private boolean atendida;

  public AlertaInactividadFarmacia(FarmaciasEntity farmacia, LocalDateTime fechaGenerada) {
    this.farmacia = farmacia;
    this.fechaGenerada = fechaGenerada;
    this.atendida = false;
  }
}