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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DisponibilidadEntity - mapea la tabla disponibilidad. La fecha_hora_reporte
 * no se mapea porque la gestiona la base de datos (DEFAULT/ON UPDATE).
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
}
