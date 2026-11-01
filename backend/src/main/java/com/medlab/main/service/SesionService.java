package com.medlab.main.service;

import com.medlab.main.dto.InicioSesionRequestDto;
import com.medlab.main.dto.SesionActualDto;
import com.medlab.main.dto.SesionResponseDto;

/**
 * Inicio y cierre de sesión (HU-2).
 */
public interface SesionService {

  String ROL_FARMACIA = "farmacia";

  /** Solo una farmacia con este estado puede iniciar sesión. */
  String ESTADO_APROBADA = "aprobada";

  /** Verifica las credenciales, crea la sesión y devuelve su token. */
  SesionResponseDto iniciarSesion(InicioSesionRequestDto datos);

  /** Devuelve la sesión dueña del token, para confirmar que sigue vigente. */
  SesionActualDto obtenerSesionActual(String token);

  /** Cierra la sesión dueña del token. */
  void cerrarSesion(String token);
}
