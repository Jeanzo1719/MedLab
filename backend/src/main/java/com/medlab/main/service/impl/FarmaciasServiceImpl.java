package com.medlab.main.service.impl;

import com.medlab.main.dto.FarmaciaCercanaDto;
import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.repository.FarmaciasRepository;
import com.medlab.main.repository.FarmaciasRepository.FarmaciaCercana;
import com.medlab.main.service.FarmaciasService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Qué hace: prepara los datos de las farmacias que se muestran en el mapa: las aprobadas del
 * mapa base y las aprobadas más cercanas que tienen un medicamento disponible.
 *
 * Cómo entenderlo: es como el empleado que filtra el directorio: primero las farmacias
 * aprobadas y después, de ellas, las que tienen el medicamento a menos de 20 km, de la más
 * cercana a la más lejana.
 *
 * Detalles técnicos: le pide las farmacias al repositorio propio FarmaciasRepository y
 * convierte cada una en su DTO. Solo lee la base de datos: no guarda ni modifica nada.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class FarmaciasServiceImpl implements FarmaciasService {

  private static final String ESTADO_APROBADA = "aprobada";

  /** Estado de disponibilidad que no cuenta en la búsqueda de farmacias cercanas: si el
   * medicamento está agotado, la farmacia no se muestra. */
  private static final String ESTADO_AGOTADO = "agotado";

  /** Distancia máxima, en kilómetros, a la que se buscan farmacias cercanas. */
  private static final double RADIO_MAXIMO_KM = 20;

  /** Cantidad máxima de farmacias cercanas devueltas, para no llenar el mapa de puntos. */
  private static final int LIMITE_FARMACIAS_CERCANAS = 20;

  private final FarmaciasRepository farmaciasRepository;

  @Override
  public List<FarmaciaMapaDto> getAllFarmaciasAprobadas() {
    return farmaciasRepository.findByEstadoAprobacionNombre(ESTADO_APROBADA).stream()
        .map(this::toFarmaciaMapaDto)
        .toList();
  }

  @Override
  public List<FarmaciaCercanaDto> getFarmaciasCercanasConDisponibilidad(Long medicamentoId,
      double latitud, double longitud) {
    return farmaciasRepository
        .findFarmaciasCercanasConDisponibilidad(ESTADO_APROBADA, ESTADO_AGOTADO, medicamentoId,
            latitud, longitud, RADIO_MAXIMO_KM, LIMITE_FARMACIAS_CERCANAS)
        .stream()
        .map(this::toFarmaciaCercanaDto)
        .toList();
  }

  private FarmaciaMapaDto toFarmaciaMapaDto(FarmaciasEntity farmacia) {
    return new FarmaciaMapaDto(farmacia.getUsuarioId(), farmacia.getNombreFarmacia(),
        farmacia.getLatitud().doubleValue(), farmacia.getLongitud().doubleValue());
  }

  /** Convierte una fila de la búsqueda de farmacias cercanas en el dato que se envía al
   * frontend: pasa la latitud y la longitud de BigDecimal a double, que es como usa el mapa. */
  private FarmaciaCercanaDto toFarmaciaCercanaDto(FarmaciaCercana farmacia) {
    return new FarmaciaCercanaDto(farmacia.getId(), farmacia.getNombre(),
        farmacia.getLatitud().doubleValue(), farmacia.getLongitud().doubleValue(),
        farmacia.getDistanciaKm(), farmacia.getDisponibilidadId());
  }
}
