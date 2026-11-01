package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.RegistroFarmaciaRequestDto;
import com.medlab.main.dto.RegistroPacienteRequestDto;
import com.medlab.main.dto.RegistroResponseDto;
import com.medlab.main.service.RegistroService;
import com.medlab.main.shared.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Registro de cuentas (HU-1). Son dos endpoints REST porque el formulario cambia según el rol:
 * uno crea pacientes/cuidadores y el otro farmacias. Todas las respuestas van envueltas en
 * ApiResponse, como el resto de la API REST; el mensaje de error lo pone
 * RegistroExceptionHandler.
 */
@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
public class RegistroController {

  private final RegistroService registroService;

  @PostMapping("/paciente")
  public ResponseEntity<ApiResponse<RegistroResponseDto>> registrarPaciente(
      @Valid @RequestBody RegistroPacienteRequestDto datos) {
    RegistroResponseDto registro = registroService.registrarPaciente(datos);

    ApiResponse<RegistroResponseDto> respuesta = ApiResponse.<RegistroResponseDto>builder()
        .code(HttpStatus.CREATED.value())
        .message("Cuenta creada correctamente")
        .data(registro)
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
  }

  @PostMapping("/farmacia")
  public ResponseEntity<ApiResponse<RegistroResponseDto>> registrarFarmacia(
      @Valid @RequestBody RegistroFarmaciaRequestDto datos) {
    RegistroResponseDto registro = registroService.registrarFarmacia(datos);

    ApiResponse<RegistroResponseDto> respuesta = ApiResponse.<RegistroResponseDto>builder()
        .code(HttpStatus.CREATED.value())
        .message("Farmacia registrada; queda en espera de aprobación")
        .data(registro)
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
  }
}
