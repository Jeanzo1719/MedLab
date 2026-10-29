package com.medlab.main.service;

import org.springframework.stereotype.Component;

import com.medlab.main.exception.AccesoPanelDenegadoException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Dice qué farmacia está usando el panel. Es el único lugar que lo decide, y todo el panel
 * le pregunta a esta clase.
 *
 * TEMPORAL: todavía no hay login (HU-2), así que la farmacia se toma del encabezado
 * X-Farmacia-Id, y cualquiera podría poner el id de otra. Cuando exista el token, solo hay
 * que cambiar obtenerId() para leer la farmacia del usuario autenticado y exigir ahí el rol
 * 'farmacia'; nada más se toca. Mientras tanto los services ya rechazan ids que no estén en
 * la tabla farmacias o que no estén aprobadas, pero eso no sustituye la autenticación.
 * No debe llegar a producción tal como está.
 */
@Component
@RequiredArgsConstructor
public class FarmaciaAutenticadaProvider {

  public static final String ENCABEZADO = "X-Farmacia-Id";

  private final HttpServletRequest solicitud;

  public Integer obtenerId() {
    String valor = solicitud.getHeader(ENCABEZADO);
    if (valor == null || valor.isBlank()) {
      throw new AccesoPanelDenegadoException("No se identificó la farmacia");
    }
    try {
      return Integer.valueOf(valor.trim());
    } catch (NumberFormatException e) {
      throw new AccesoPanelDenegadoException("Identificador de farmacia inválido");
    }
  }
}