package com.medlab.main.repository;

import com.medlab.main.entity.FarmaciasEntity;
import java.math.BigDecimal;
import java.util.List;

public interface FarmaciasRepository {

  List<FarmaciasEntity> findByEstadoAprobacionNombre(String nombreEstado);

  List<FarmaciaCercana> findFarmaciasCercanasConDisponibilidad(String estadoFarmacia,
      String estadoExcluido, Long medicamentoId, double latitud, double longitud,
      double radioKm, int limite);

  /** Una fila de la busqueda de farmacias cercanas: sus datos basicos, la distancia
   * calculada hasta la posicion del usuario, en kilometros, y el id de su reporte de
   * disponibilidad del medicamento buscado. */
  interface FarmaciaCercana {

    Integer getId();

    String getNombre();

    BigDecimal getLatitud();

    BigDecimal getLongitud();

    Double getDistanciaKm();

    Integer getDisponibilidadId();
  }
}
