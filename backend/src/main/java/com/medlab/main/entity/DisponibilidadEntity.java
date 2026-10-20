package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DisponibilidadEntity - mapea la tabla disponibilidad: el reporte que una farmacia
 * hace sobre un medicamento (qué tiene, cuánto cuesta y cuándo se actualizó).
 * La fecha_hora_reporte la actualiza la base de datos sola (DEFAULT/ON UPDATE),
 * por eso solo se mapea de lectura.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "disponibilidad")
public class DisponibilidadEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private FarmaciasEntity farmacia;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medicamento_id", nullable = false)
  private MedicamentosEntity medicamento;

  @Column(name = "cantidad_disponible", nullable = false)
  private Integer cantidadDisponible;

  @Column(name = "precio", nullable = false, precision = 10, scale = 2)
  private BigDecimal precio;

  @Column(name = "estado_disponibilidad_id", nullable = false)
  private Integer estadoDisponibilidadId;

  /** Fecha y hora del último reporte. La actualiza la base de datos sola. */
  @Column(name = "fecha_hora_reporte", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHoraReporte;
}
