package com.medlab.main.controller;

import java.util.List;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import lombok.RequiredArgsConstructor;

/**
 * 
 * la puerta de entrada al backend. Recibe los pedidos del frontend y se los pasa 
 * al service. No tiene lógica propia
 * 
 */
@Controller
@RequiredArgsConstructor
public class FarmaciaController {

	private final FarmaciaService farmaciaService;

	/**
	 * 
	 * responde con la lista de farmacias aprobadas cuando el frontend se
	 * suscribe a /app/farmacias/aprobadas
	 * 
	 * {@code @SubscribeMapping("/farmacias/aprobadas")} conecta el destino con este 
	 * método. El método le pide la lista al service y la devuelve. Spring la 
	 * convierte a JSON y se la envía solo a quien se suscribió, una sola vez
	 * 
	 */
	@SubscribeMapping("/farmacias/aprobadas")
	public List<FarmaciaMapaDto> listarAprobadas() {
		return farmaciaService.listarAprobadasParaMapa();
	}
}
