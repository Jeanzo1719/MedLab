import { Injectable } from "@angular/core";

import { Sesion } from "../models/sesion.model";

@Injectable({ providedIn: "root" })
export class SesionService {
  /**
   * Synchronous and local on purpose: the landing must never wait on, or
   * depend on, the users database to decide whether to redirect.
   */
  obtenerSesionActiva(): Sesion | null {
    return null;
  }
  //TODO: hay que conectarlo con la base de datos
  // 1. En la HU de autenticación, el backend emite un token (JWT) al iniciar sesión, con el rol
  //    del usuario y su fecha de expiración, y registra la sesión en la colección "sesiones".
  // 2. El frontend guarda ese token al iniciar sesión (por ejemplo, en sessionStorage).
  // 3. Aquí: leer el token, descartarlo si expiró y devolver { rol } a partir de su contenido.
  //    Todo es local, sin llamar al backend, para que la landing siga sin depender de la base
  //    de datos de usuarios.
  // 4. Crear las rutas de RUTAS_PANEL antes de devolver sesiones reales.
}
