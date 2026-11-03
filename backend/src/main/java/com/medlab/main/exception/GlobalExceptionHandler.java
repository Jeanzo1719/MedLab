package com.medlab.main.exception;

import com.medlab.main.shared.ApiResponse;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo global de excepciones: convierte las excepciones de negocio en
 * respuestas estandarizadas ApiResponse con el codigo HTTP correspondiente.
 * El mensaje visible al usuario ("sin resultados") lo define el frontend
 * a partir del codigo 404.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(NoSuchElementException ex) {
    ApiResponse<Void> notFoundResponse = ApiResponse.<Void>builder()
        .code(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .data(null)
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
  }

  /**
   * La base de datos rechazó el cambio porque choca con lo que ya tiene: por ejemplo, un
   * medicamento repetido o uno que todavía usan otros registros. Se responde 409 sin mostrar el
   * detalle técnico del error.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    ApiResponse<Void> conflictResponse = ApiResponse.<Void>builder()
        .code(HttpStatus.CONFLICT.value())
        .message("La operación choca con datos que ya existen")
        .data(null)
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
  }
}
