package com.medlab.main.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.medlab.main.dto.FarmaciaCercanaDto;
import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

/**
 * 
 * la puerta de entrada al backend. Recibe los pedidos del frontend y se los pasa 
 * al service. No tiene lógica propia
 * 
 */
@Controller
@Validated
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

	/**
	 * 
	 * responde con las farmacias aprobadas más cercanas que tienen el medicamento
	 * disponible, cuando el frontend se suscribe a /app/farmacias/cercanas
	 * 
	 * los datos llegan como datos extra de la suscripción: medicamentoId (el
	 * medicamento que se busca), latitud y longitud (dónde está el usuario).
	 * Las anotaciones revisan que vengan y que sean válidos: el id mayor que 0, la
	 * latitud entre -90 y 90 y la longitud entre -180 y 180. Si algo no cumple, no
	 * se busca nada y el error queda en el log del backend. Si todo está bien, se
	 * lo pasa al service
	 * 
	 */
	@SubscribeMapping("/farmacias/cercanas")
	public List<FarmaciaCercanaDto> listarCercanas(@Header("medicamentoId") @NotNull @Positive Integer medicamentoId,
			@Header("latitud") @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitud,
			@Header("longitud") @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitud) {
		return farmaciaService.listarCercanasConDisponibilidad(medicamentoId, latitud, longitud);
	}
}
