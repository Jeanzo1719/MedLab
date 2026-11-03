package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.CalificacionRequestDto;
import com.medlab.main.security.UsuarioAutenticado;
import com.medlab.main.service.CalificacionService;
import com.medlab.main.shared.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Qué hace: recibe la calificación que un paciente/cuidador deja sobre el reporte de una
 * farmacia (HU-3).
 *
 * Cómo entenderlo: es la urna donde el usuario deposita su voto. El usuario no escribe quién
 * es: el sistema lo sabe por la sesión con la que entró.
 *
 * Detalles técnicos: SecurityConfig exige el rol paciente para POST /api/calificaciones. El id
 * del usuario se toma de UsuarioAutenticado, nunca del cuerpo de la petición. El service guarda
 * el voto y avisa a quienes están viendo el medicamento por /topic/medicamentos/{id}.
 */
@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

  private final CalificacionService calificacionService;

  /**
   * Guarda o actualiza la calificación del usuario autenticado.
   *
   * datos: el reporte calificado y el tipo elegido.
   *
   * Responde 200 con un mensaje cuando se guarda. Si el reporte no existe, el manejador global
   * responde 404.
   */
  @PostMapping
  public ApiResponse<Void> calificar(@Valid @RequestBody CalificacionRequestDto datos) {
    UsuarioAutenticado usuario = UsuarioAutenticado.actual();
    calificacionService.saveCalificacion(datos.disponibilidadId(), usuario.usuarioId(),
        datos.tipoCalificacionId());

    return ApiResponse.<Void>builder()
        .code(HttpStatus.OK.value())
        .message("Calificación guardada correctamente")
        .data(null)
        .build();
  }
}
