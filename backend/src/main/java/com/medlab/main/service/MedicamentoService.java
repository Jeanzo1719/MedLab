package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

	private final MedicamentoRepository medicamentoRepository;

	/**
	 * Searches by commercial name or active ingredient. The text arrives already stripped and validated by
	 * {@link com.medlab.main.dto.MedicamentoBusquedaFiltroDto}.
	 */
	public List<MedicamentoBusquedaDto> buscar(String texto) {
		return medicamentoRepository
				.findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(texto, texto).stream()
				.map(this::aBusquedaDto).toList();
	}

	private MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
		return new MedicamentoBusquedaDto(medicamento.getId(), medicamento.getNombreComercial(),
				medicamento.getPrincipioActivo(), medicamento.getPresentacion(), medicamento.getFormaFarmaceutica());
	}
}
