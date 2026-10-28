package com.medlab.main.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabla farmacias. Mapea solo las columnas que usa la landing. */
@Entity
@Table(name = "farmacias")
@Getter
@Setter
@NoArgsConstructor
public class Farmacia {

  /** Id de la farmacia. */
  @Id
  @Column(name = "usuario_id")
  private Integer usuarioId;

  /** Nombre de la farmacia. */
  @Column(name = "nombre_farmacia", nullable = false, length = 150)
  private String nombreFarmacia;

  /** Coordenadas del marcador en el mapa. */
  @Column(precision = 10, scale = 8)
  private BigDecimal latitud;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitud;

  /** Estado (pendiente, aprobada o suspendida); se carga solo cuando se usa. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estado_aprobacion_id")
  private EstadoFarmacia estadoAprobacion;
}