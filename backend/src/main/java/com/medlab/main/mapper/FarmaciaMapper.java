package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.Farmacia;

/**
 * Converts pharmacy entities into the DTOs the API exposes.
 * <p>
 * Keeping the conversion here, instead of inside the service, gives each layer a single job: the service decides which
 * pharmacies to return, and this class decides which of their fields leave the backend. It only copies the public
 * fields, so internal data (owner, approval state) can never leak by accident.
 */
@Component
public class FarmaciaMapper {

	/** Public map view of a pharmacy: id, name and coordinates only */
	public FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getId(), farmacia.getNombreFarmacia(), farmacia.getLatitud(),
				farmacia.getLongitud());
	}
}
