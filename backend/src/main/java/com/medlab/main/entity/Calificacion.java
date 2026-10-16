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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Qué hace: representa una fila de la tabla calificaciones, es decir, la opinión de un usuario
 * sobre un reporte de disponibilidad de una farmacia.
 *
 * Cómo entenderlo: es como una reseña en una tienda en línea: alguien dice "sí había" o
 * "no había" sobre lo que la farmacia publicó.
 *
 * Detalles técnicos: cada calificación pertenece a una Disponibilidad y tiene un
 * TipoCalificacion. La base de datos asigna el id y la fecha_hora al guardar, por eso
 * fechaHora es de solo lectura desde Java. La tabla también guarda el usuario que calificó
 * (usuario_id), que esta entity no mapea porque HU-4 no lo necesita.
 * CalificacionRepository cuenta estas filas por reporte y ConfianzaService las usa para
 * calcular la confianza de cada reporte.
 */
@Entity
@Table(name = "calificaciones")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calificacion {

  /** Número que identifica a la calificación. Lo asigna la base de datos (1, 2, 3...). */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Reporte de disponibilidad que se está calificando. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "disponibilidad_id", nullable = false)
  private Disponibilidad disponibilidad;

  /** Tipo de calificación elegido, por ejemplo "Si hay". */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tipo_calificacion_id", nullable = false)
  private TipoCalificacion tipoCalificacion;

  /** Fecha y hora en que se calificó. La pone la base de datos al guardar. */
  @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHora;
}