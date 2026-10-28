package com.medlab.main.repository;

import com.medlab.main.entity.FarmaciasEntity;
import java.math.BigDecimal;
import java.util.List;

public interface FarmaciasRepository {

  List<FarmaciasEntity> findByEstadoAprobacionNombre(String nombreEstado);

  List<FarmaciaCercana> findFarmaciasCercanasConDisponibilidad(String estadoFarmacia,
      String estadoExcluido, Long medicamentoId, double latitud, double longitud,
      double radioKm, int limite);

  /** Una fila de la busqueda de farmacias cercanas: sus datos basicos y la distancia
   * calculada hasta la posicion del usuario, en kilometros. */
  interface FarmaciaCercana {

    Integer getId();

    String getNombre();

    BigDecimal getLatitud();

    BigDecimal getLongitud();

    Double getDistanciaKm();

    Integer getDisponibilidadId();
  }
}
