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

	private static final String ESTADO_AGOTADO = "agotado";

	private static final double RADIO_MAXIMO_KM = 20;

	private static final int LIMITE_FARMACIAS_CERCANAS = 20;

	private final FarmaciaRepository farmaciaRepository;

	public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
		return farmaciaRepository.findByEstadoAprobacionNombre(ESTADO_APROBADA).stream().map(this::aMapaDto).toList();
	}

	public List<FarmaciaCercanaDto> listarCercanasConDisponibilidad(Integer medicamentoId, double latitud,
			double longitud) {
		return farmaciaRepository.buscarCercanasConDisponibilidad(ESTADO_APROBADA, ESTADO_AGOTADO, medicamentoId,
				latitud, longitud, RADIO_MAXIMO_KM, LIMITE_FARMACIAS_CERCANAS).stream().map(this::aCercanaDto).toList();
	}

	private FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getUsuarioId(), farmacia.getNombreFarmacia(),
				farmacia.getLatitud().doubleValue(), farmacia.getLongitud().doubleValue());
	}

	private FarmaciaCercanaDto aCercanaDto(FarmaciaCercana farmacia) {
		return new FarmaciaCercanaDto(farmacia.getId(), farmacia.getNombre(), farmacia.getLatitud().doubleValue(),
				farmacia.getLongitud().doubleValue(), farmacia.getDistanciaKm());
	}
}
