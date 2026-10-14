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

/** Tabla medicamentos. */
@Entity
@Table(name = "medicamentos")
@Getter
@Setter
@NoArgsConstructor
public class Medicamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** Nombre de marca (ej. "Dolex"). */
  @Column(name = "nombre_comercial", nullable = false, length = 100)
  private String nombreComercial;

  /** Sustancia activa (ej. "Acetaminofén"); el buscador también filtra por aquí. */
  @Column(name = "principio_activo", nullable = false, length = 150)
  private String principioActivo;

  /** Guarda el id de la categoría; JPA carga el resto solo cuando se usa. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  /** Empaque (ej. "500 mg x 10"). */
  @Column(name = "presentacion", nullable = false, length = 100)
  private String presentacion;

  /** Forma del medicamento (ej. tableta); funciona igual que categoria. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "forma_farmaceutica_id", nullable = false)
  private FormaFarmaceutica formaFarmaceutica;
}