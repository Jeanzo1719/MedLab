package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.mapper.MedicamentoMapper;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

	private final MedicamentoRepository medicamentoRepository;
	private final MedicamentoMapper medicamentoMapper;

	public List<MedicamentoBusquedaDto> buscar(String texto) {
		return medicamentoRepository
				.findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(texto, texto).stream()
				.map(medicamentoMapper::aBusquedaDto).toList();
	}
}
