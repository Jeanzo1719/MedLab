package com.medlab.main.dto;

import java.util.List;

/** Opciones de los selectores del formulario de HU-7: categorías, formas y estados. */
public record CatalogosDto(List<Opcion> categorias, List<Opcion> formasFarmaceuticas,
    List<String> estados) {

  /** Una opción de lista desplegable: el id que se envía y el nombre que se muestra. */
  public record Opcion(Integer id, String nombre) {
  }
}