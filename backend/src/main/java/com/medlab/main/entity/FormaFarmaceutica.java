package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabla formas_farmaceuticas: cómo viene el medicamento (ej. tableta, jarabe). */
@Entity
@Table(name = "formas_farmaceuticas")
@Getter
@Setter
@NoArgsConstructor
public class FormaFarmaceutica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nombre", nullable = false, length = 30)
  private String nombre;
}