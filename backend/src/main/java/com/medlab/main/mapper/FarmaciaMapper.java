package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.Farmacia;

/**
 * Convierte las entidades de farmacia en los DTO que expone la API (capa de mappers).
 * <p>
 * Para qué sirve: al tener la conversión aquí, y no dentro del servicio, cada capa hace una sola cosa: el servicio
 * decide qué farmacias devolver y esta clase decide qué campos salen del backend.
 * <p>
 * Cómo funciona: copia solo los campos públicos, así los datos internos (dueño, estado de aprobación) nunca pueden
 * filtrarse por accidente. Es un {@code @Component}, así que {@link com.medlab.main.service.FarmaciaService} lo recibe
 * por inyección.
 */
@Component
public class FarmaciaMapper {

	/** Vista pública de una farmacia para el mapa: solo id, nombre y coordenadas */
	public FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getId(), farmacia.getNombreFarmacia(), farmacia.getLatitud(),
				farmacia.getLongitud());
	}
}
