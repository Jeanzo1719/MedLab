package com.medlab.main.repository;

import java.util.Collection;
import java.util.List;

public interface CalificacionesRepository {

  List<CalificacionReciente> findCalificacionesRecientesByDisponibilidad(
      Collection<Integer> disponibilidadIds, int limite);

  /** Una calificacion reciente: el id del reporte al que pertenece y el nombre de su tipo
   * ("Si hay", "Bajo pedido" o "No hay"). */
  interface CalificacionReciente {

    Integer getDisponibilidadId();

    String getTipo();
  }
}
