package com.medlab.main.repository;

import java.util.Collection;
import java.util.List;

public interface CalificacionesRepository {

  List<CalificacionReciente> findCalificacionesRecientesByDisponibilidad(
      Collection<Integer> disponibilidadIds, int limite);

  /** Guarda la calificación de un usuario sobre un reporte, o cambia la que ya tenía en ese
   * mismo reporte. Devuelve cuántas filas cambió la base de datos. */
  int saveCalificacion(Integer disponibilidadId, Integer usuarioId, Integer tipoCalificacionId);

  /** Una calificacion reciente: el id del reporte al que pertenece y el nombre de su tipo
   * ("Si hay", "Bajo pedido" o "No hay"). */
  interface CalificacionReciente {

    Integer getDisponibilidadId();

    String getTipo();
  }
}
