package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;

/**
 * Convierte las entidades de medicamento en los DTO que expone la API (capa de mappers).
 * <p>
 * Para qué sirve: al tener la conversión aquí, y no dentro del servicio, cada capa hace una sola cosa: el servicio
 * decide qué medicamentos coinciden con una búsqueda y esta clase decide qué campos salen del backend (la categoría,
 * por ejemplo, no forma parte del resultado).
 * <p>
 * Cómo funciona: es un {@code @Component}, así que {@link com.medlab.main.service.MedicamentoService} lo recibe por
 * inyección.
 */
@Component
public class MedicamentoMapper {

	/** Vista de un medicamento en los resultados de búsqueda */
	public MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
		return new MedicamentoBusquedaDto(medicamento.getId(), medicamento.getNombreComercial(),
				medicamento.getPrincipioActivo(), medicamento.getPresentacion(), medicamento.getFormaFarmaceutica());
	}
}
