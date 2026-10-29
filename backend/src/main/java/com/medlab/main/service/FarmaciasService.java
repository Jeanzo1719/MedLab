package com.medlab.main.service;

import com.medlab.main.dto.FarmaciaCercanaDto;
import com.medlab.main.dto.FarmaciaMapaDto;
import java.util.List;

/**
 * Prepara los datos de las farmacias que se muestran en el mapa de la landing.
 */
public interface FarmaciasService {

  List<FarmaciaMapaDto> getAllFarmaciasAprobadas();

  List<FarmaciaCercanaDto> getFarmaciasCercanasConDisponibilidad(Long medicamentoId,
      double latitud, double longitud);
}
