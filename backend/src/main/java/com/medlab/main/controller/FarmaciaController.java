package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/farmacias")
@RequiredArgsConstructor
public class FarmaciaController {

	private final FarmaciaService farmaciaService;

	@GetMapping("/aprobadas")
	public ResponseEntity<List<FarmaciaMapaDto>> listarAprobadas() {
		return ResponseEntity.ok(farmaciaService.listarAprobadasParaMapa());
	}
}
