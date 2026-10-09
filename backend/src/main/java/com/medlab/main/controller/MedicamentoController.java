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

/**
 * 
 * el controller de los medicamentos. Atiende las búsquedas del buscador 
 * de la landing, responde con los medicamentos que coinciden con el texto
 * que escribió el usuario, cuando el frontend se suscribe a /app/medicamentos/buscar
 *  
 */
@Controller
@Validated
@RequiredArgsConstructor
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	/**
	 *
	 * recibe el texto que escribió el usuario y responde con la lista de
	 * medicamentos que coinciden, una sola vez y solo a quien se suscribió
	 *
	 * el texto llega como un dato extra de la suscripción llamado q (de query,
	 * "consulta"). {@code @NotBlank} y {@code @Size} revisan que no esté vacío y
	 * que tenga entre 2 y 150 caracteres; si no cumple, no se busca nada y el
	 * error queda en el log del backend. Si es válido, se lo pasa al service
	 *
	 */
	@SubscribeMapping("/medicamentos/buscar")
	public List<MedicamentoBusquedaDto> buscar(@Header("q") @NotBlank @Size(min = 2, max = 150) String q) {
		return medicamentoService.buscar(q);
	}
}
