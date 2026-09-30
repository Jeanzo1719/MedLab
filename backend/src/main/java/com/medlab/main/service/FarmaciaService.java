package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.repository.FarmaciaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmaciaService {

	private static final String ESTADO_APROBADA = "aprobada";

	private final FarmaciaRepository farmaciaRepository;

	public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
		return farmaciaRepository.findByEstadoAprobacionNombre(ESTADO_APROBADA).stream().map(this::aMapaDto).toList();
	}

	private FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
		return new FarmaciaMapaDto(farmacia.getUsuarioId(), farmacia.getNombreFarmacia(),
				farmacia.getLatitud().doubleValue(), farmacia.getLongitud().doubleValue());
	}
}
