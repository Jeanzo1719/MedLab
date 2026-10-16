package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.FarmaciaCercanaDto;
import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.repository.FarmaciaRepository;
import com.medlab.main.repository.FarmaciaRepository.FarmaciaCercana;

import lombok.RequiredArgsConstructor;

/**
 * 
 * este archivo es el service de farmacias, prepara los datos de las 
 * farmacias que se muestran en el mapa
 * 
 * se hace en tres pasos.
 * a. Le pide al repositorio las farmacias con estado "aprobada".
 * b. Recorre la lista (stream()) y convierte cada farmacia en un 
 * FarmaciaMapaDto (map(this::aMapaDto)).
 * c. Junta los resultados en una lista nueva (toList()).
 * 
 */
@Service
@RequiredArgsConstructor
public class FarmaciaService {

	private static final String ESTADO_APROBADA = "aprobada";

	/**
	 * 
	 * el estado de disponibilidad que no cuenta en la búsqueda de farmacias
	 * cercanas: si el medicamento está agotado, la farmacia no se muestra
	 * 
	 */
	private static final String ESTADO_AGOTADO = "agotado";

	/**
	 * 
	 * la distancia máxima, en kilómetros, a la que se buscan farmacias cercanas
	 * 
	 */
	private static final double RADIO_MAXIMO_KM = 20;

	/**
	 * 
	 * la cantidad máxima de farmacias cercanas que se devuelven, para no llenar
	 * el mapa de puntos
	 * 
	 */
	private static final int LIMITE_FARMACIAS_CERCANAS = 20;

	private final FarmaciaRepository farmaciaRepository;

	public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
		return farmaciaRepository.findByEstadoAprobacionNombre(ESTADO_APROBADA).stream().map(this::aMapaDto).toList();
	}

	/**
	 * 
	 * devuelve las farmacias aprobadas más cercanas al usuario que tienen el
	 * medicamento disponible, ordenadas de la más cercana a la más lejana
	 * 
	 * le pide al repositorio las farmacias aprobadas que no tienen el medicamento
	 * agotado, a 20 km o menos y como máximo 20. Después convierte cada una en un
	 * FarmaciaCercanaDto y las junta en una lista
	 * 
	 */
	public List<FarmaciaCercanaDto> listarCercanasConDisponibilidad(Integer medicamentoId, double latitud,
			double longitud) {
		return farmaciaRepository.buscarCercanasConDisponibilidad(ESTADO_APROBADA, ESTADO_AGOTADO, medicamentoId,
				latitud, longitud, RADIO_MAXIMO_KM, LIMITE_FARMACIAS_CERCANAS).stream().map(this::aCercanaDto).toList();
	}

	private FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getUsuarioId(), farmacia.getNombreFarmacia(),
				farmacia.getLatitud().doubleValue(), farmacia.getLongitud().doubleValue());
	}

	/**
	 * 
	 * convierte una fila de la búsqueda de farmacias cercanas en los datos que
	 * se envían al frontend
	 * 
	 * copia el id, el nombre y la distancia, y pasa la latitud y la longitud de
	 * BigDecimal a números normales (double), que es como las usa el mapa
	 * 
	 */
	private FarmaciaCercanaDto aCercanaDto(FarmaciaCercana farmacia) {
		return new FarmaciaCercanaDto(farmacia.getId(), farmacia.getNombre(), farmacia.getLatitud().doubleValue(),
				farmacia.getLongitud().doubleValue(), farmacia.getDistanciaKm());
	}
}
