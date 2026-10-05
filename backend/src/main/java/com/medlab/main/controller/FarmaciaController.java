package com.medlab.main.controller;

import java.util.List;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FarmaciaController {

	private final FarmaciaService farmaciaService;

	@SubscribeMapping("/farmacias/aprobadas")
	public List<FarmaciaMapaDto> listarAprobadas() {
		return farmaciaService.listarAprobadasParaMapa();
	}
}
