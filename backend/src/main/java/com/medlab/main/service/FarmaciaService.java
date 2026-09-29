package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.mapper.FarmaciaMapper;
import com.medlab.main.repository.FarmaciaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio de las farmacias (capa de servicios).
 * <p>
 * Para qué sirve: contiene la regla de qué farmacias son públicas. Solo se muestran las aprobadas; las pendientes y las
 * suspendidas quedan ocultas para los visitantes.
 * <p>
 * Cómo funciona: pide a {@link FarmaciaRepository} las farmacias aprobadas y convierte cada una en un DTO público con
 * {@link FarmaciaMapper}. No sabe nada de HTTP, que es trabajo del controller, ni del almacenamiento, que es trabajo
 * del repositorio.
 */
@Service
@RequiredArgsConstructor
public class FarmaciaService {

	private final FarmaciaRepository farmaciaRepository;
	private final FarmaciaMapper farmaciaMapper;

	/** Solo las farmacias aprobadas son públicas; las pendientes y las suspendidas quedan ocultas */
	public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
		return farmaciaRepository.findByEstadoAprobacion(EstadoAprobacion.APROBADA).stream()
				.map(farmaciaMapper::aMapaDto).toList();
	}
}
