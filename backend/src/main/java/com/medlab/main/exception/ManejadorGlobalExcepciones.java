package com.medlab.main.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de errores de todos los controllers REST (capa de excepciones).
 * <p>
 * Para qué sirve: sin esta clase, cada error lo responde la página /error por defecto de Spring, cuya forma cambia
 * según el tipo de error y que, con devtools activo, incluso trae el stack trace de Java. Con ella, todo error sale de
 * la API como un JSON "problem detail" estándar (RFC 9457: {@code type}, {@code title}, {@code status}, {@code detail},
 * {@code instance}), así los clientes siempre leen la misma estructura.
 * <p>
 * Cómo funciona:
 * <ul>
 * <li>Extiende {@link ResponseEntityExceptionHandler}, que ya convierte las excepciones propias de Spring MVC (404 ruta
 * inexistente, 405 método no permitido, 400 petición mal formada, etc.) en problem details.</li>
 * <li>Los errores de validación de los DTO de entrada ({@code @Valid}) se sobrescriben para listar además, en
 * {@code errores}, cada campo inválido y su mensaje.</li>
 * <li>Cualquier otra excepción inesperada se convierte en un 500 genérico: la causa se registra en el log del servidor
 * y nunca se envía al cliente.</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class ManejadorGlobalExcepciones extends ResponseEntityExceptionHandler {

	/** 400 cuando un DTO de entrada con {@code @Valid} incumple sus restricciones; lista cada campo inválido */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, "La solicitud tiene datos inválidos.");
		List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> Map.of("campo", error.getField(), "mensaje", String.valueOf(error.getDefaultMessage())))
				.toList();
		problema.setProperty("errores", errores);
		return handleExceptionInternal(ex, problema, headers, status, request);
	}

	/** 500 para todo lo que no se maneja arriba; el detalle queda solo en el log del servidor */
	@ExceptionHandler(Exception.class)
	public ProblemDetail manejarErrorInesperado(Exception ex) {
		log.error("Error no controlado al atender la petición", ex);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocurrió un error inesperado. Intenta de nuevo más tarde.");
	}
}
