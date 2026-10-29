package com.medlab.main.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Página de resultados con formato propio. Se evita devolver Page directo porque Spring
 * avisa que su JSON no es estable entre versiones y el frontend dependería de él.
 */
public record PaginaDto<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

  public static <T> PaginaDto<T> de(Page<T> pagina) {
    return new PaginaDto<>(pagina.getContent(), pagina.getNumber(), pagina.getSize(),
        pagina.getTotalElements(), pagina.getTotalPages());
  }
}