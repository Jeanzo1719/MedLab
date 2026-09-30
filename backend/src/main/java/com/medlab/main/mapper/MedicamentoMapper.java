package com.medlab.main.mapper;

import org.springframework.stereotype.Component;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;

@Component
public class MedicamentoMapper {

	public MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
		return new MedicamentoBusquedaDto(medicamento.getId(), medicamento.getNombreComercial(),
				medicamento.getPrincipioActivo(), medicamento.getPresentacion(), medicamento.getFormaFarmaceutica());
	}
}
