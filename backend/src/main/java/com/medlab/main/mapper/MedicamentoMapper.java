package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;

/**
 * Converts medicine entities into the DTOs the API exposes.
 * <p>
 * Keeping the conversion here, instead of inside the service, gives each layer a single job: the service decides which
 * medicines match a search, and this class decides which of their fields leave the backend (the category, for example,
 * is not part of the search result).
 */
@Component
public class MedicamentoMapper {

	/** Search result view of a medicine */
	public MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
		return new MedicamentoBusquedaDto(medicamento.getId(), medicamento.getNombreComercial(),
				medicamento.getPrincipioActivo(), medicamento.getPresentacion(), medicamento.getFormaFarmaceutica());
	}
}
