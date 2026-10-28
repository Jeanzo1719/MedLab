package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.medlab.main.dto.ErrorDto;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.exception.RecursoNoEncontradoException;

/**
 * Traduce las excepciones del panel administrador a códigos HTTP. Es un handler aparte del de
 * la farmacia y está limitado a PanelAdminController, para no cambiar cómo responden los
 * controllers de los demás.
 */
@RestControllerAdvice(assignableTypes = PanelAdminController.class)
public class PanelAdminExceptionHandler {

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

  /** Un id o parámetro numérico con texto (por ejemplo /farmacias/abc/auditoria). */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto parametroInvalido(MethodArgumentTypeMismatchException e) {
    return new ErrorDto("El parámetro '" + e.getName() + "' no es válido");
  }
}