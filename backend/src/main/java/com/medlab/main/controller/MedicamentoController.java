package com.medlab.main.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.service.MedicamentoService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Controller
@Validated
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	@SubscribeMapping("/medicamentos/buscar")
	public List<MedicamentoBusquedaDto> buscar(@Header("q") @NotBlank @Size(min = 2, max = 150) String q) {
		return medicamentoService.buscar(q);
	}
}
