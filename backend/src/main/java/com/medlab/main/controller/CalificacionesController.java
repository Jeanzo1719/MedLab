package com.medlab.main.controller;

import com.medlab.main.exception.DisponibilidadNotFoundException;
import com.medlab.main.security.UsuarioAutenticado;
import com.medlab.main.service.CalificacionService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Controller
@Validated
@AllArgsConstructor
public class CalificacionesController {

  private final CalificacionService calificacionService;

  @MessageMapping("/calificaciones")
  public void saveCalificacion(
      @Header("disponibilidadId") @NotNull @Positive Integer disponibilidadId,
      @Header("tipoCalificacionId") @NotNull @Positive Integer tipoCalificacionId,
      Authentication autenticacion) {
    UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacion.getPrincipal();
    calificacionService.saveCalificacion(disponibilidadId, usuario.usuarioId(), tipoCalificacionId);
  }

  @MessageExceptionHandler({DisponibilidadNotFoundException.class,
      DataIntegrityViolationException.class})
  public void handleCalificacionException(RuntimeException ex) {
    log.warn("No se pudo guardar la calificación: {}", ex.getMessage());
  }
}
