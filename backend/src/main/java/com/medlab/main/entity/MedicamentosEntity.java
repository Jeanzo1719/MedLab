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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla medicamentos. La unicidad la define la base de datos sobre el compuesto
 * (nombre_comercial, principio_activo, presentacion, forma_farmaceutica_id),
 * por eso las columnas individuales no tienen unique.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "medicamentos")
public class MedicamentosEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "nombre_comercial", nullable = false, length = 100)
  private String nombreComercial;

  @Column(name = "principio_activo", nullable = false, length = 150)
  private String principioActivo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private CategoriasEntity categoria;

  @Column(name = "presentacion", nullable = false, length = 100)
  private String presentacion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "forma_farmaceutica_id", nullable = false)
  private FormasFarmaceuticasEntity formaFarmaceutica;
}
