package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmacias")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Farmacia {

  @Id
  @Column(name = "usuario_id")
  private Integer usuarioId;

  @Column(name = "nombre_farmacia", nullable = false, length = 150)
  private String nombreFarmacia;
}