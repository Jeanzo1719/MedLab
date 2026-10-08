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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicamentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nombre_comercial", nullable = false, length = 100)
  private String nombreComercial;

  @Column(name = "principio_activo", nullable = false, length = 150)
  private String principioActivo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  @Column(name = "presentacion", nullable = false, length = 100)
  private String presentacion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "forma_farmaceutica_id", nullable = false)
  private FormaFarmaceutica formaFarmaceutica;
}