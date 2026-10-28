package com.medlab.main.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.medlab.main.dto.ErrorDto;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.exception.RecursoNoEncontradoException;

/**
 * Convierte las excepciones del panel en respuestas con código HTTP y mensaje. Está limitado
 * a PanelFarmaciaController a propósito: así no cambia cómo responden los controllers de los
 * demás compañeros.
 */
@RestControllerAdvice(assignableTypes = PanelFarmaciaController.class)
public class PanelFarmaciaExceptionHandler {

  @ExceptionHandler(AccesoPanelDenegadoException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorDto accesoDenegado(AccesoPanelDenegadoException e) {
    return new ErrorDto(e.getMessage());
  }

  @ExceptionHandler(DatosInvalidosException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto datosInvalidos(DatosInvalidosException e) {
    return new ErrorDto(e.getMessage());
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto noEncontrado(RecursoNoEncontradoException e) {
    return new ErrorDto(e.getMessage());
  }

  /** Falló una anotación del DTO (cantidad negativa, precio vacío...): se listan los campos. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto validacion(MethodArgumentNotValidException e) {
    String detalle = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));
    return new ErrorDto(detalle);
  }

  /** JSON mal formado o con tipos incorrectos (por ejemplo cantidad = "abc"). */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto cuerpoIlegible(HttpMessageNotReadableException e) {
    return new ErrorDto("El cuerpo de la solicitud no es válido");
  }
}