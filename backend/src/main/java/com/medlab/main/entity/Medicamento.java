package com.medlab.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Medicine as it will be stored in the "medicamentos" MongoDB collection.
 * Category and dosage form are embedded strings instead of lookup tables.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

  private String id;
  private String nombreComercial;
  private String principioActivo;
  private String categoria;
  private String presentacion;
  private String formaFarmaceutica;
}
