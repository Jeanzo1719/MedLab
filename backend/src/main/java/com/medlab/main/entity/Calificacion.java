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
@Table(name = "calificaciones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "disponibilidad_id", nullable = false)
  private Disponibilidad disponibilidad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tipo_calificacion_id", nullable = false)
  private TipoCalificacion tipoCalificacion;

  @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaHora;
}