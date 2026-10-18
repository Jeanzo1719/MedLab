package com.medlab.main.exception;

import com.medlab.main.shared.ApiResponse;
import java.util.NoSuchElementException;
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
    ApiResponse<Void> response = ApiResponse.<Void>builder()
        .code(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .data(null)
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }
}
