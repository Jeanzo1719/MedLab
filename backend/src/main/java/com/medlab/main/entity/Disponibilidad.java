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
 * Qué hace: representa una fila de la tabla disponibilidad, es decir, el reporte que una
 * farmacia hace sobre un medicamento.
 *
 * Cómo entenderlo: es como la etiqueta de un estante: dice qué farmacia tiene qué
 * medicamento y cuándo se actualizó esa información por última vez.
 *
 * Detalles técnicos: une un Medicamento con una Farmacia, y la base de datos garantiza una
 * sola fila por cada pareja farmacia-medicamento. La columna fecha_hora_reporte la
 * actualiza la base de datos sola cada vez que la fila cambia, por eso fechaHoraReporte es
 * de solo lectura desde Java. Esta entity solo mapea las columnas que usa HU-4; la tabla
 * también guarda cantidad, precio y estado de disponibilidad. DisponibilidadRepository la
 * lee y MedicamentoDetalleService la convierte en un DisponibilidadReporteDto.
 */
@Entity
@Table(name = "disponibilidad")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Disponibilidad {

  /** Número que identifica al reporte. Lo asigna la base de datos (1, 2, 3...). */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** Farmacia que hace el reporte. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private Farmacia farmacia;

  /** Medicamento sobre el que trata el reporte. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medicamento_id", nullable = false)
  private Medicamento medicamento;

  /** Fecha y hora del último reporte. La actualiza la base de datos sola. */
  @Column(name = "fecha_hora_reporte", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHoraReporte;
}