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

/**
 * EstadosFarmaciaEntity
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estados_farmacia")
public class EstadosFarmaciaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "nombre", nullable = false, length = 30, unique = true)
  private String nombre;
}
