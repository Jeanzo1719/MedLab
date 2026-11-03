package com.medlab.main.dto;

import java.util.List;

/**
 * Qué hace: lleva una página de resultados del buscador y los datos necesarios para pintar la
 * paginación.
 *
 * Cómo entenderlo: es como la hoja de un listado que dice "estos son los resultados de esta
 * página, en total hay tantos y quedan tantas páginas".
 *
 * Detalles técnicos: MedicamentosServiceImpl lo arma a partir de la página que devuelve
 * MedicamentosRepository. Se devuelve por WebSocket como DTO crudo, sin ApiResponse, igual
 * que el resto de las respuestas del buscador.
 *
 * resultados: los medicamentos de esta página.
 * total: cuántos medicamentos cumplen los filtros en total.
 * pagina: el número de esta página (desde 0).
 * tamano: cuántos resultados caben por página.
 * totalPaginas: cuántas páginas hay en total.
 * hayMas: si existe una página siguiente.
 */
public record PaginaMedicamentosDto(List<MedicamentoBusquedaDto> resultados, long total, int pagina,
    int tamano, int totalPaginas, boolean hayMas) {

  /** Página vacía para cuando no hay criterios o no hay coincidencias. */
  public static PaginaMedicamentosDto vacia(BusquedaMedicamentosDto filtros) {
    return new PaginaMedicamentosDto(List.of(), 0, filtros.pagina(), filtros.tamano(), 0, false);
  }
}
