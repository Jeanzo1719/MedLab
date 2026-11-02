package com.medlab.main.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.medlab.main.exception.AccesoPanelDenegadoException;

/**
 * El usuario que está detrás de la solicitud actual, tal como lo dejó el filtro de sesión.
 *
 * Lo usan los providers de los paneles para saber qué farmacia o qué administrador está
 * trabajando, sin volver a leer encabezados que cualquiera podría falsificar.
 */
public record UsuarioAutenticado(Integer usuarioId, Integer rolId, String rol, String token) {

  /** Devuelve el usuario de la solicitud; si no hay sesión, corta con 403. */
  public static UsuarioAutenticado actual() {
    Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
    if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioAutenticado usuario)) {
      throw new AccesoPanelDenegadoException("No se identificó la sesión");
    }
    return usuario;
  }
}
