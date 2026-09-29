package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.config.RutasApi;
import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.dto.MedicamentoBusquedaFiltroDto;
import com.medlab.main.service.MedicamentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Public medicine endpoints used by the landing page search box.
 * <p>
 * Endpoints under /api/public need no authentication (visitor mode) and only allow GET requests from the origins
 * configured in {@link com.medlab.main.config.CorsConfig}.
 */
@RestController
@RequestMapping(RutasApi.MEDICAMENTOS_PUBLICOS)
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	/**
	 * Searches medicines whose brand name or active ingredient contains the given text, ignoring case and accents.
	 * <p>
	 * {@code GET /api/public/medicamentos/buscar?q=acetaminofen}
	 *
	 * @param filtro search text, sent as the {@code q} query parameter; it must have between 2 and 150 characters once
	 *               stripped
	 * @return 200 OK with the matching medicines, or an empty list if nothing matches; 400 Bad Request if the text is
	 *         missing, too short or too long
	 */
	@GetMapping("/buscar")
	public ResponseEntity<List<MedicamentoBusquedaDto>> buscar(@Valid MedicamentoBusquedaFiltroDto filtro) {
		return ResponseEntity.ok(medicamentoService.buscar(filtro.q()));
	}
}
