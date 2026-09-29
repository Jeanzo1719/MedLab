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
 * Global error handler for every REST controller.
 * <p>
 * What it is for: without it, each error is answered by Spring's default /error page, whose shape changes between error
 * types and which, with devtools on, even includes the Java stack trace. This class makes every error leave the API as
 * a standard RFC 9457 "problem detail" JSON ({@code type}, {@code title}, {@code status}, {@code detail},
 * {@code instance}), so clients always parse the same structure.
 * <p>
 * How it works:
 * <ul>
 * <li>It extends {@link ResponseEntityExceptionHandler}, which already turns Spring MVC's own exceptions (404 route not
 * found, 405 method not allowed, 400 malformed request, ...) into problem details.</li>
 * <li>Validation errors of request DTOs ({@code @Valid}) are overridden to also list each invalid field and its message
 * under {@code errores}.</li>
 * <li>Any other unexpected exception becomes a generic 500: the cause is logged on the server and never sent to the
 * client.</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class ManejadorGlobalExcepciones extends ResponseEntityExceptionHandler {

	/** 400 when a {@code @Valid} request DTO breaks its constraints, listing every invalid field */
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

	/** 500 for anything not handled above; details stay in the server log */
	@ExceptionHandler(Exception.class)
	public ProblemDetail manejarErrorInesperado(Exception ex) {
		log.error("Error no controlado al atender la petición", ex);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocurrió un error inesperado. Intenta de nuevo más tarde.");
	}
}
