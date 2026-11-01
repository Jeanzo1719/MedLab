package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.medlab.main.dto.ErrorDto;
import com.medlab.main.exception.IdentificacionDuplicadaException;

/**
 * Traduce los errores del registro a códigos HTTP y a un mensaje corto que el formulario puede
 * mostrar tal cual. Está limitado a RegistroController para no cambiar las respuestas de los
 * demás controllers.
 */
@RestControllerAdvice(assignableTypes = RegistroController.class)
public class RegistroExceptionHandler {

  /** La cédula ya está en uso: 409, que el frontend muestra junto al campo. */
  @ExceptionHandler(IdentificacionDuplicadaException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorDto identificacionDuplicada(IdentificacionDuplicadaException e) {
    return new ErrorDto(e.getMessage());
  }

  /** Una anotación del DTO (@Pattern, @Size, @NotBlank...) rechazó un campo. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .filter((texto) -> texto != null && !texto.isBlank())
        .findFirst()
        .orElse("Los datos del registro no son válidos");
    return new ErrorDto(mensaje);
  }

  /** El cuerpo no es un JSON válido o le falta un campo obligatorio. */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto cuerpoIlegible(HttpMessageNotReadableException e) {
    return new ErrorDto("El formulario llegó incompleto o con un formato que no se entiende");
  }
}
