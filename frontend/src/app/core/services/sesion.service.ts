import { Injectable } from "@angular/core";

import { Sesion } from "../models/sesion.model";

/**
 * 
 * el service de la sesión del usuario: dice si hay alguien con sesión iniciada
 * y qué tipo de usuario es
 * 
 */
@Injectable({ providedIn: "root" })
export class SesionService {
  /**
   * 
   * devuelve la sesión del usuario, o null si no hay nadie con sesión iniciada
   * 
   * por ahora siempre devuelve null, porque el login todavía no existe. Los pasos
   * para conectarlo están en el TODO de abajo
   * 
   */
  obtenerSesionActiva(): Sesion | null {
    return null;
  }
  //TODO: hay que conectarlo con la base de datos
  // 1. En la HU de autenticación, el backend emite un token (JWT) al iniciar sesión, con el rol
  //    del usuario y su fecha de expiración, y registra la sesión en la tabla "sesiones".
  // 2. El frontend guarda ese token al iniciar sesión (por ejemplo, en sessionStorage).
  // 3. Aquí: leer el token, descartarlo si expiró y devolver { rol } a partir de su contenido.
  //    Todo es local, sin llamar al backend, para que la landing siga sin depender de la base
  //    de datos de usuarios.
  // 4. Antes de devolver sesiones reales, declarar en app.routes.ts las rutas de los paneles
  //    (/paciente, /farmacia, /administrador), que es a donde redirige redirigirAutenticadoGuard.
}
