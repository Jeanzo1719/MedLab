package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.mapper.MedicamentoMapper;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio de los medicamentos (capa de servicios).
 * <p>
 * Para qué sirve: realiza la búsqueda de los visitantes por nombre comercial o principio activo.
 * <p>
 * Cómo funciona: recibe un texto ya validado por {@link com.medlab.main.dto.MedicamentoBusquedaFiltroDto}, pide a
 * {@link MedicamentoRepository} las coincidencias en cualquiera de los dos campos y convierte cada una en un DTO de
 * búsqueda con {@link MedicamentoMapper}. No sabe nada de HTTP ni del almacenamiento.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoService {

	private final MedicamentoRepository medicamentoRepository;
	private final MedicamentoMapper medicamentoMapper;

	/**
	 * Busca por nombre comercial o principio activo. El texto llega ya sin espacios en los extremos y validado por
	 * {@link com.medlab.main.dto.MedicamentoBusquedaFiltroDto}.
	 */
	public List<MedicamentoBusquedaDto> buscar(String texto) {
		return medicamentoRepository
				.findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(texto, texto).stream()
				.map(medicamentoMapper::aBusquedaDto).toList();
	}
}
