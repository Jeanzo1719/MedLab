package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.mapper.FarmaciaMapper;
import com.medlab.main.repository.FarmaciaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmaciaService {

	private final FarmaciaRepository farmaciaRepository;
	private final FarmaciaMapper farmaciaMapper;

	public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
		return farmaciaRepository.findByEstadoAprobacion(EstadoAprobacion.APROBADA).stream()
				.map(farmaciaMapper::aMapaDto).toList();
	}
}
