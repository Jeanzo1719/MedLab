package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.config.RutasApi;
import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import lombok.RequiredArgsConstructor;

/**
 * Public pharmacy endpoints used by the landing page map.
 * <p>
 * Endpoints under /api/public need no authentication (visitor mode) and only allow GET requests from the origins
 * configured in {@link com.medlab.main.config.CorsConfig}.
 */
@RestController
@RequestMapping(RutasApi.FARMACIAS_PUBLICAS)
@RequiredArgsConstructor
public class FarmaciaController {

	private final FarmaciaService farmaciaService;

	/**
	 * Lists the approved pharmacies to be shown as markers on the map. Pending and suspended pharmacies are never
	 * returned.
	 * <p>
	 * {@code GET /api/public/farmacias/aprobadas}
	 *
	 * @return 200 OK with the approved pharmacies, or an empty list if there are none
	 */
	@GetMapping("/aprobadas")
	public ResponseEntity<List<FarmaciaMapaDto>> listarAprobadas() {
		return ResponseEntity.ok(farmaciaService.listarAprobadasParaMapa());
	}
}
