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
 * Controller REST de los endpoints públicos de medicamentos (capa de controllers).
 * <p>
 * Para qué sirve: atiende el buscador de medicamentos de la landing, que los visitantes usan sin cuenta.
 * <p>
 * Cómo funciona: Spring arma un {@link MedicamentoBusquedaFiltroDto} con los parámetros de la URL y {@code @Valid}
 * revisa sus restricciones antes de ejecutar este código. Las peticiones inválidas nunca llegan al servicio: las
 * responde con un 400 {@link com.medlab.main.exception.ManejadorGlobalExcepciones}. Las válidas se delegan en
 * {@link MedicamentoService} y el resultado se envuelve en un {@link ResponseEntity}. Los endpoints bajo
 * {@link RutasApi#PUBLICA} no requieren autenticación (modo visitante) y solo aceptan GET desde los orígenes
 * configurados en {@link com.medlab.main.config.CorsConfig}.
 */
@RestController
@RequestMapping(RutasApi.MEDICAMENTOS_PUBLICOS)
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	/**
	 * Busca medicamentos cuyo nombre comercial o principio activo contenga el texto, sin distinguir mayúsculas ni
	 * tildes.
	 * <p>
	 * {@code GET /api/public/medicamentos/buscar?q=acetaminofen}
	 *
	 * @param filtro texto de búsqueda, enviado en el parámetro {@code q}; debe tener entre 2 y 150 caracteres sin
	 *               contar los espacios de los extremos
	 * @return 200 OK con los medicamentos encontrados, o una lista vacía si no hay coincidencias; 400 Bad Request si el
	 *         texto falta, es muy corto o es muy largo
	 */
	@GetMapping("/buscar")
	public ResponseEntity<List<MedicamentoBusquedaDto>> buscar(@Valid MedicamentoBusquedaFiltroDto filtro) {
		return ResponseEntity.ok(medicamentoService.buscar(filtro.q()));
	}
}
