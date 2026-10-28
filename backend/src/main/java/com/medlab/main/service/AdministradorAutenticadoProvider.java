package com.medlab.main.service;

import org.springframework.stereotype.Component;

import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.repository.HistorialAdminRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Dice qué administrador está usando el panel (HU-9). Es el único lugar que lo decide.
 *
 * TEMPORAL: sin login (HU-2) el id sale del encabezado X-Admin-Id, y cualquiera podría poner
 * el de otro. Lo único que se comprueba es que ese usuario exista y tenga el rol
 * 'administrador'. Cuando exista el token, solo hay que cambiar obtenerId() para leer el
 * usuario autenticado. No debe llegar a producción tal como está.
 */
@Component
@RequiredArgsConstructor
public class AdministradorAutenticadoProvider {

  public static final String ENCABEZADO = "X-Admin-Id";
  private static final String ROL_ADMINISTRADOR = "administrador";

  private final HttpServletRequest solicitud;
  private final HistorialAdminRepository historialRepository;

  public Integer obtenerId() {
    String valor = solicitud.getHeader(ENCABEZADO);
    if (valor == null || valor.isBlank()) {
      throw new AccesoPanelDenegadoException("No se identificó al administrador");
    }

    Integer id;
    try {
      id = Integer.valueOf(valor.trim());
    } catch (NumberFormatException e) {
      throw new AccesoPanelDenegadoException("Identificador de administrador inválido");
    }

    if (historialRepository.contarConRol(id, ROL_ADMINISTRADOR) == 0) {
      throw new AccesoPanelDenegadoException("El usuario no es administrador");
    }
    return id;
  }
}