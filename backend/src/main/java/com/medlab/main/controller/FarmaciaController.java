package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST de los endpoints públicos de farmacias (capa de controllers).
 * <p>
 * Para qué sirve: entrega las farmacias aprobadas que la landing dibuja como marcadores en su mapa.
 * <p>
 * Cómo funciona: solo traduce HTTP a llamadas al servicio. Recibe la petición, delega en {@link FarmaciaService} y
 * envuelve el resultado en un {@link ResponseEntity}. No tiene reglas de negocio ni accede al repositorio. Los
 * endpoints bajo {@code /api/public} no requieren autenticación (modo visitante) y solo aceptan GET desde los orígenes
 * configurados en {@link com.medlab.main.config.SecurityConfig}.
 */
@RestController
@RequestMapping("/api/public/farmacias")
@RequiredArgsConstructor
public class FarmaciaController {

	private final FarmaciaService farmaciaService;

	/**
	 * Lista las farmacias aprobadas que se muestran como marcadores en el mapa. Nunca devuelve farmacias pendientes ni
	 * suspendidas.
	 * <p>
	 * {@code GET /api/public/farmacias/aprobadas}
	 *
	 * @return 200 OK con las farmacias aprobadas, o una lista vacía si no hay ninguna
	 */
	@GetMapping("/aprobadas")
	public ResponseEntity<List<FarmaciaMapaDto>> listarAprobadas() {
		return ResponseEntity.ok(farmaciaService.listarAprobadasParaMapa());
	}
}
