package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.Farmacia;

@Component
public class FarmaciaMapper {

	public FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getId(), farmacia.getNombreFarmacia(), farmacia.getLatitud(),
				farmacia.getLongitud());
	}
}
