import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, map } from "rxjs";

import { environment } from "../../../environments/environment";

/**
 *
 * los dos roles que se pueden crear desde el registro. "cuidador" no es un rol
 * aparte: comparte la cuenta y los datos del paciente
 *
 */
export type RolRegistro = "paciente" | "farmacia";

/**
 *
 * los datos del formulario cuando el rol es paciente/cuidador. Es el espejo de
 * RegistroPacienteRequestDto
 *
 */
export interface RegistroPaciente {
  identificacion: string;
  correo: string;
  contrasena: string;
  telefono: string;
  epsAfiliada: string;
  primerNombre: string;
  segundoNombre: string | null;
  apellidos: string;
}

/**
 *
 * los datos del formulario cuando el rol es farmacia. Es el espejo de
 * RegistroFarmaciaRequestDto
 *
 */
export interface RegistroFarmacia {
  correo: string;
  contrasena: string;
  telefono: string;
  epsAfiliada: string;
  nombreFarmacia: string;
  tipoViaPrincipal: string;
  numeroViaPrincipal: string;
  letraViaPrincipal: string | null;
  numeroViaGeneradora: string;
  letraViaGeneradora: string | null;
  numeroPredio: string;
  complemento: string | null;
  nit: string;
  rut: string;
  matriculaMercantil: string;
  identificacionDirectorTecnico: string;
  licenciaSanitaria: string;
  latitud: number;
  longitud: number;
}

/**
 *
 * lo que responde el backend al crear la cuenta. Es el espejo de
 * RegistroResponseDto; el estado de una farmacia es "pendiente" y el de un
 * paciente, "activo"
 *
 * identificacion es con la que la cuenta inicia sesión: la cédula del paciente o
 * el código que el sistema le genera a la farmacia
 *
 */
export interface RegistroCreado {
  id: number;
  identificacion: string;
  rol: RolRegistro;
  estado: string;
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
 * el service que crea las cuentas desde el formulario de registro
 *
 */
@Injectable({ providedIn: "root" })
export class RegistroService {
  private readonly http: HttpClient = inject(HttpClient);

  /**
   *
   * crea la cuenta de un paciente/cuidador con su cédula
   *
   */
  registrarPaciente(datos: RegistroPaciente): Observable<RegistroCreado> {
    return this.http
      .post<RespuestaApi<RegistroCreado>>(`${environment.apiUrl}/registro/paciente`, datos)
      .pipe(map((respuesta: RespuestaApi<RegistroCreado>): RegistroCreado => respuesta.data));
  }

  /**
   *
   * crea la cuenta de una farmacia. El backend le genera un UUID como
   * identificación y la deja pendiente de aprobación
   *
   */
  registrarFarmacia(datos: RegistroFarmacia): Observable<RegistroCreado> {
    return this.http
      .post<RespuestaApi<RegistroCreado>>(`${environment.apiUrl}/registro/farmacia`, datos)
      .pipe(map((respuesta: RespuestaApi<RegistroCreado>): RegistroCreado => respuesta.data));
  }
}

/**
 *
 * traduce el error del backend al mensaje que se muestra encima del formulario.
 * El backend responde ErrorDto ({ mensaje }) para los errores del registro
 *
 */
export function mensajeDeRegistroError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return "No hay conexión con el servidor";
    }
    const mensaje: unknown = error.error?.mensaje;
    if (typeof mensaje === "string" && mensaje.length > 0) {
      return mensaje;
    }
  }
  return "No se pudo completar el registro. Inténtalo de nuevo.";
}
