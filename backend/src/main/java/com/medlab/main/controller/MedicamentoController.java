package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.dto.MedicamentoBusquedaFiltroDto;
import com.medlab.main.service.MedicamentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	@GetMapping("/buscar")
	public ResponseEntity<List<MedicamentoBusquedaDto>> buscar(@Valid MedicamentoBusquedaFiltroDto filtro) {
		return ResponseEntity.ok(medicamentoService.buscar(filtro.q()));
	}
}
