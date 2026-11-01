package com.medlab.main.service;

import org.springframework.stereotype.Component;

import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.security.UsuarioAutenticado;

/**
 * Dice qué farmacia está usando el panel. Es el único lugar que lo decide, y todo el panel le
 * pregunta a esta clase.
 *
 * Desde HU-2 la farmacia sale del token de la sesión: SecurityConfig ya exige el rol 'farmacia'
 * para /api/panel-farmacia/**, y aquí se vuelve a comprobar antes de devolver el id. El id del
 * usuario es el mismo id de la farmacia (la tabla farmacias comparte la llave con usuarios).
 */
@Component
public class FarmaciaAutenticadaProvider {

  public Integer obtenerId() {
    UsuarioAutenticado usuario = UsuarioAutenticado.actual();
    if (!SesionService.ROL_FARMACIA.equals(usuario.rol())) {
      throw new AccesoPanelDenegadoException("La sesión no es de una farmacia");
    }
    return usuario.usuarioId();
  }
}
