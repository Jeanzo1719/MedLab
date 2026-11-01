package com.medlab.main.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.InicioSesionRequestDto;
import com.medlab.main.dto.SesionActualDto;
import com.medlab.main.dto.SesionResponseDto;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.security.TokenBearer;
import com.medlab.main.service.SesionService;
import com.medlab.main.shared.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Inicio y cierre de sesión (HU-2).
 *
 * POST /api/sesiones es la única ruta pública de este controller: recibe identificación y
 * contraseña y devuelve el token. Las otras dos trabajan con el token del encabezado
 * Authorization: Bearer.
 */
@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

  private final SesionService sesionService;

  @PostMapping
  public ApiResponse<SesionResponseDto> iniciarSesion(
      @Valid @RequestBody InicioSesionRequestDto datos) {
    return ApiResponse.<SesionResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Sesión iniciada correctamente")
        .data(sesionService.iniciarSesion(datos))
        .build();
  }

  @GetMapping("/actual")
  public ApiResponse<SesionActualDto> sesionActual(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String autorizacion) {
    return ApiResponse.<SesionActualDto>builder()
        .code(HttpStatus.OK.value())
        .message("Sesión vigente")
        .data(sesionService.obtenerSesionActual(token(autorizacion)))
        .build();
  }

  @DeleteMapping("/actual")
  public ApiResponse<Void> cerrarSesion(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String autorizacion) {
    sesionService.cerrarSesion(token(autorizacion));
    return ApiResponse.<Void>builder()
        .code(HttpStatus.OK.value())
        .message("Sesión cerrada correctamente")
        .data(null)
        .build();
  }

  private static String token(String autorizacion) {
    String token = TokenBearer.extraer(autorizacion);
    if (token == null) {
      throw new SesionInvalidaException();
    }
    return token;
  }
}
