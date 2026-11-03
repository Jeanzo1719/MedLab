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

/**
 * Qué hace: recibe por WebSocket lo que un paciente dice de un reporte de disponibilidad ("Si
 * hay", "Bajo pedido" o "No hay") y se lo pasa a CalificacionService.
 *
 * Cómo entenderlo: es la urna de la encuesta: recibe el voto y sabe quién lo entrega, porque el
 * usuario sale de la sesión y no de lo que diga el mensaje. Nadie puede votar por otro.
 *
 * Detalles técnicos: SesionStompInterceptor ya revisó que el mensaje trae una sesión de paciente
 * antes de que llegue aquí. No responde nada directamente: al guardar, el detalle del medicamento
 * se publica en /topic/medicamentos/{id}, y el frontend toma ese mensaje como confirmación.
 */
@Slf4j
@Controller
@Validated
@AllArgsConstructor
public class CalificacionesController {

  private final CalificacionService calificacionService;

  /**
   * Guarda la calificación cuando el frontend envía un mensaje a /app/calificaciones.
   *
   * disponibilidadId: id del reporte que se califica.
   * tipoCalificacionId: id del tipo elegido (1 "Si hay", 2 "Bajo pedido", 3 "No hay").
   * autenticacion: la sesión del paciente, que dejó SesionStompInterceptor.
   */
  @MessageMapping("/calificaciones")
  public void saveCalificacion(
      @Header("disponibilidadId") @NotNull @Positive Integer disponibilidadId,
      @Header("tipoCalificacionId") @NotNull @Positive Integer tipoCalificacionId,
      Authentication autenticacion) {
    UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacion.getPrincipal();
    calificacionService.saveCalificacion(disponibilidadId, usuario.usuarioId(), tipoCalificacionId);
  }

  /** Si el reporte o el tipo de calificación no existen, deja solo un aviso en el log del backend.
   * El cliente no recibe respuesta, así que el frontend avisa el error cuando se acaba la espera. */
  @MessageExceptionHandler({DisponibilidadNotFoundException.class,
      DataIntegrityViolationException.class})
  public void handleCalificacionException(RuntimeException ex) {
    log.warn("No se pudo guardar la calificación: {}", ex.getMessage());
  }
}
