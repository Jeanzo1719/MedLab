package com.medlab.main.service;

import org.springframework.stereotype.Component;

import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.security.UsuarioAutenticado;

/**
 * Dice qué administrador está usando el panel (HU-9). Es el único lugar que lo decide.
 *
 * Desde HU-2 el administrador sale del token de la sesión: SecurityConfig ya exige el rol
 * 'administrador' para /api/panel-admin/**, y aquí se vuelve a comprobar antes de devolver el id.
 */
@Component
public class AdministradorAutenticadoProvider {

  private static final String ROL_ADMINISTRADOR = "administrador";

  public Integer obtenerId() {
    UsuarioAutenticado usuario = UsuarioAutenticado.actual();
    if (!ROL_ADMINISTRADOR.equals(usuario.rol())) {
      throw new AccesoPanelDenegadoException("La sesión no es de un administrador");
    }
    return usuario.usuarioId();
  }
}
