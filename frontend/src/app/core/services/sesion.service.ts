import { Injectable } from "@angular/core";

import { Sesion } from "../models/sesion.model";

/**
 * Servicio de sesión del usuario (capa core, servicios).
 *
 * Qué es: el punto único donde la app pregunta si hay alguien con sesión
 * iniciada.
 *
 * Cómo funciona: por ahora siempre devuelve null (visitante), porque la
 * autenticación todavía no existe. El TODO de abajo explica cómo conectarlo.
 *
 * Para qué sirve: redirigirAutenticadoGuard lo usa para decidir si envía al
 * usuario a su panel.
 */
@Injectable({ providedIn: "root" })
export class SesionService {
  /**
   * Síncrono y local a propósito: la landing nunca debe esperar ni depender de
   * la base de datos de usuarios para decidir si redirige.
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
  // 4. Antes de devolver sesiones reales, declarar en app.routes.ts las rutas de los paneles
  //    (/paciente, /farmacia, /administrador), que es a donde redirige redirigirAutenticadoGuard.
}
