package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.medlab.main.dto.ErrorDto;
import com.medlab.main.exception.CredencialesInvalidasException;
import com.medlab.main.exception.FarmaciaNoAprobadaException;
import com.medlab.main.exception.SesionInvalidaException;

/**
 * Traduce los errores de la sesión a códigos HTTP y a un mensaje que el formulario puede mostrar
 * tal cual. Está limitado a SesionController para no cambiar las respuestas de los demás.
 */
@RestControllerAdvice(assignableTypes = SesionController.class)
public class SesionExceptionHandler {

  /** Identificación o contraseña incorrectas: 401 y el mismo mensaje para las dos. */
  @ExceptionHandler(CredencialesInvalidasException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDto credencialesInvalidas(CredencialesInvalidasException e) {
    return new ErrorDto(e.getMessage());
  }

  /** La farmacia todavía no puede entrar: 403 con el mensaje de su estado. */
  @ExceptionHandler(FarmaciaNoAprobadaException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorDto farmaciaNoAprobada(FarmaciaNoAprobadaException e) {
    return new ErrorDto(e.getMessage());
  }

  /** El token no se pudo leer o su sesión ya no vale: 401. */
  @ExceptionHandler(SesionInvalidaException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDto sesionInvalida(SesionInvalidaException e) {
    return new ErrorDto(e.getMessage());
  }

  /** Una anotación del DTO rechazó la identificación o la contraseña. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .filter((texto) -> texto != null && !texto.isBlank())
        .findFirst()
        .orElse("Los datos de la sesión no son válidos");
    return new ErrorDto(mensaje);
  }

  /** El cuerpo no es un JSON válido o le falta un campo obligatorio. */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto cuerpoIlegible(HttpMessageNotReadableException e) {
    return new ErrorDto("El formulario llegó incompleto o con un formato que no se entiende");
  }
}
