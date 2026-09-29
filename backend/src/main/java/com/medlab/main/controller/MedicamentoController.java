package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.service.MedicamentoService;

import lombok.RequiredArgsConstructor;

/**
 * Public medicine endpoints used by the landing page search box.
 * <p>
 * Endpoints under /api/public need no authentication (visitor mode) and only allow GET requests from the origins
 * configured in {@link com.medlab.main.config.CorsConfig}.
 */
@RestController
@RequestMapping("/api/public/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	/**
	 * Searches medicines whose brand name or active ingredient contains the given text, ignoring case and accents.
	 * <p>
	 * {@code GET /api/public/medicamentos?q=acetaminofen}
	 *
	 * @param texto text to search for, sent as the {@code q} query parameter; texts shorter than two characters return
	 *              no results
	 * @return 200 OK with the matching medicines, or an empty list if nothing matches
	 */
	@GetMapping
	public ResponseEntity<List<MedicamentoBusquedaDto>> buscar(
			@RequestParam(name = "q", defaultValue = "") String texto) {
		return ResponseEntity.ok(medicamentoService.buscar(texto));
	}
}
