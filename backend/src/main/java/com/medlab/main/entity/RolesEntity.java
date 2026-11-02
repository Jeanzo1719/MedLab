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
 * RolesEntity - mapea la tabla roles: paciente, farmacia y administrador.
 * El rol decide qué formulario de registro se usa y a qué panel entra el usuario.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RolesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "nombre", nullable = false, length = 30)
  private String nombre;
}
