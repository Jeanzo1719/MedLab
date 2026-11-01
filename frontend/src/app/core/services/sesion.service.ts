import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Injectable, Signal, WritableSignal, computed, inject, signal } from "@angular/core";
import { Observable, catchError, map, of } from "rxjs";

import { environment } from "../../../environments/environment";

/**
 *
 * los tipos de usuario que va a tener MedLab. Solo acepta estos tres textos
 * exactos; si en el código se escribe otro, por ejemplo "admin", TypeScript
 * marca el error
 *
 */
export type RolUsuario = "paciente" | "farmacia" | "administrador";

/**
 *
 * la sesión de un usuario que inició sesión. El rol y el vencimiento salen del
 * propio token firmado, no de lo que diga el navegador, así que no se pueden
 * cambiar desde la consola
 *
 */
export interface Sesion {
  token: string;
  rol: RolUsuario;
  identificacion: string;
  expiraEn: string;
}

/**
 *
 * el sobre de todas las respuestas REST (shared/ApiResponse del backend)
 *
 */
interface RespuestaApi<T> {
  code: number;
  message: string;
  data: T;
}

/**
 *
 * lo que responde el backend al iniciar sesión. Es el espejo de
 * SesionResponseDto
 *
 */
interface SesionRespuesta {
  token: string;
  rol: RolUsuario;
  identificacion: string;
  expiraEn: string;
}

/**
 *
 * lo que se guarda en el navegador entre recargas. El rol y la expiración no se
 * guardan: se vuelven a leer del token, que viene firmado
 *
 */
interface SesionGuardada {
  token: string;
  identificacion: string;
}

/**
 *
 * el contenido del JWT que interesa al frontend
 *
 */
interface ContenidoToken {
  sub: string;
  rolId: number;
  rol: string;
  exp: number;
}

const CLAVE_SESION = "medlab.sesion";
const ROLES: RolUsuario[] = ["paciente", "farmacia", "administrador"];

/**
 *
 * a dónde va cada rol después de iniciar sesión
 *
 */
export function rutaInicio(rol: RolUsuario): string {
  if (rol === "farmacia") {
    return "/farmacia";
  }
  if (rol === "administrador") {
    return "/administrador";
  }
  return "/paciente";
}

/**
 *
 * el service de la sesión del usuario: inicia sesión, la guarda, dice si sigue
 * vigente y la cierra
 *
 */
@Injectable({ providedIn: "root" })
export class SesionService {
  private readonly http: HttpClient = inject(HttpClient);

  private readonly sesion: WritableSignal<Sesion | null> = signal(this.leerDelAlmacen());

  /**
   *
   * la sesión actual, o null si no hay nadie con sesión iniciada
   *
   */
  readonly sesionActual: Signal<Sesion | null> = this.sesion.asReadonly();

  readonly autenticado: Signal<boolean> = computed(() => this.sesion() !== null);

  /**
   *
   * pide el token al backend con la identificación y la contraseña, y guarda la
   * sesión. El token dura dos semanas
   *
   */
  iniciarSesion(identificacion: string, contrasena: string): Observable<Sesion> {
    return this.http
      .post<RespuestaApi<SesionRespuesta>>(`${environment.apiUrl}/sesiones`, {
        identificacion,
        contrasena,
      })
      .pipe(
        map((respuesta: RespuestaApi<SesionRespuesta>): SesionRespuesta => respuesta.data),
        map((datos: SesionRespuesta): Sesion =>
          this.guardar({
            token: datos.token,
            rol: datos.rol,
            identificacion: datos.identificacion,
            expiraEn: datos.expiraEn,
          }),
        ),
      );
  }

  /**
   *
   * cierra la sesión: borra el token del navegador de inmediato y avisa al
   * backend para que la fila de sesiones quede inactiva. Si el aviso falla, la
   * sesión local ya quedó cerrada
   *
   */
  cerrarSesion(): void {
    const token: string | null = this.token();
    this.limpiarSesionLocal();
    if (!token) {
      return;
    }
    this.http
      .delete<void>(`${environment.apiUrl}/sesiones/actual`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` }),
      })
      .pipe(catchError(() => of(undefined)))
      .subscribe();
  }

  /**
   *
   * devuelve la sesión si sigue vigente, o null si no hay o ya venció
   *
   */
  obtenerSesionActiva(): Sesion | null {
    return this.sesion();
  }

  /** El token para el interceptor, o null si no hay sesión. */
  token(): string | null {
    return this.sesion()?.token ?? null;
  }

  /**
   *
   * borra la sesión del navegador sin llamar al backend. La usa el interceptor
   * cuando el servidor responde 401
   *
   */
  limpiarSesionLocal(): void {
    localStorage.removeItem(CLAVE_SESION);
    this.sesion.set(null);
  }

  private guardar(sesion: Sesion): Sesion {
    const guardada: SesionGuardada = {
      token: sesion.token,
      identificacion: sesion.identificacion,
    };
    localStorage.setItem(CLAVE_SESION, JSON.stringify(guardada));
    this.sesion.set(sesion);
    return sesion;
  }

  /**
   *
   * lee la sesión guardada y la descarta si el token no se entiende, si el rol
   * no es válido o si ya venció
   *
   */
  private leerDelAlmacen(): Sesion | null {
    const guardada: SesionGuardada | null = this.leerGuardada();
    if (!guardada) {
      return null;
    }

    const contenido: ContenidoToken | null = this.decodificar(guardada.token);
    if (!contenido || !this.esRol(contenido.rol) || contenido.exp * 1000 <= Date.now()) {
      localStorage.removeItem(CLAVE_SESION);
      return null;
    }

    return {
      token: guardada.token,
      rol: contenido.rol,
      identificacion: guardada.identificacion,
      expiraEn: new Date(contenido.exp * 1000).toISOString(),
    };
  }

  private leerGuardada(): SesionGuardada | null {
    const texto: string | null = localStorage.getItem(CLAVE_SESION);
    if (!texto) {
      return null;
    }
    try {
      const datos: unknown = JSON.parse(texto);
      if (typeof datos !== "object" || datos === null) {
        return null;
      }
      const guardada = datos as Record<string, unknown>;
      const token: unknown = guardada["token"];
      const identificacion: unknown = guardada["identificacion"];
      if (typeof token !== "string" || typeof identificacion !== "string") {
        return null;
      }
      return { token, identificacion };
    } catch {
      return null;
    }
  }

  /**
   *
   * lee el payload del JWT sin verificar la firma: el frontend solo lo usa para
   * saber el rol y cuándo vence. Quien de verdad valida el token es el backend
   *
   */
  private decodificar(token: string): ContenidoToken | null {
    const partes: string[] = token.split(".");
    if (partes.length !== 3) {
      return null;
    }

    try {
      const base64: string = partes[1].replace(/-/g, "+").replace(/_/g, "/");
      const relleno: string = "=".repeat((4 - (base64.length % 4)) % 4);
      const json: string = decodeURIComponent(
        atob(base64 + relleno)
          .split("")
          .map((caracter: string): string => `%${caracter.charCodeAt(0).toString(16).padStart(2, "0")}`)
          .join(""),
      );
      const contenido: unknown = JSON.parse(json);
      if (typeof contenido !== "object" || contenido === null) {
        return null;
      }

      const datos = contenido as Record<string, unknown>;
      const sub: unknown = datos["sub"];
      const rol: unknown = datos["rol"];
      const exp: unknown = datos["exp"];
      const rolId: unknown = datos["rolId"];
      if (typeof sub !== "string" || typeof rol !== "string" || typeof exp !== "number") {
        return null;
      }
      return { sub, rol, rolId: Number(rolId), exp };
    } catch {
      return null;
    }
  }

  private esRol(valor: string): valor is RolUsuario {
    return ROLES.includes(valor as RolUsuario);
  }
}

/**
 *
 * traduce el error del backend al mensaje que se muestra en el formulario de
 * inicio de sesión. El backend responde ErrorDto ({ mensaje })
 *
 */
export function mensajeDeSesionError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return "No hay conexión con el servidor";
    }
    const mensaje: unknown = error.error?.mensaje;
    if (typeof mensaje === "string" && mensaje.length > 0) {
      return mensaje;
    }
  }
  return "No se pudo iniciar sesión. Inténtalo de nuevo.";
}
