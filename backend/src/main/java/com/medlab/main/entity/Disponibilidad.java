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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  // La BD la actualiza sola (ON UPDATE CURRENT_TIMESTAMP): solo lectura desde Java.
  @Column(name = "fecha_hora_reporte", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHoraReporte;
}
