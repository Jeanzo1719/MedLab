package com.medlab.main.entity;

import java.math.BigDecimal;
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
 * Lo que una farmacia tiene de un medicamento: cantidad, precio y estado.
 * Hay una sola fila por pareja farmacia-medicamento.
 *
 * Solo se lee con JPA. Las escrituras (HU-11 y HU-7) pasan por el upsert nativo de
 * DisponibilidadRepository, que es atómico: si dos reportes llegan a la vez no se
 * duplica la fila. Por eso aquí no hay setters.
 */
@Entity
@Table(name = "disponibilidad")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Disponibilidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private Farmacia farmacia;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medicamento_id", nullable = false)
  private Medicamento medicamento;

  @Column(name = "cantidad_disponible", nullable = false)
  private Integer cantidadDisponible;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal precio;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_disponibilidad_id", nullable = false)
  private EstadoDisponibilidad estadoDisponibilidad;

  /** La base de datos la actualiza sola en cada cambio; desde Java es solo lectura. */
  @Column(name = "fecha_hora_reporte", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHoraReporte;
}