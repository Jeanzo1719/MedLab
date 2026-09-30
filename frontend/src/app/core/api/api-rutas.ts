import { environment } from "../../../environments/environment";

/**
 * Endpoints del backend que usa el frontend, en un solo lugar (capa core, api).
 *
 * Qué es: la lista de rutas que declaran los controllers del backend.
 *
 * Cómo funciona: cada entrada une la URL base de la API del entorno
 * (environment.apiUrl) con la ruta del área pública del backend.
 *
 * Para qué sirve: los servicios nunca arman URL a mano. Si el backend cambia una
 * ruta, solo cambia este archivo, y se pueden revisar de un vistazo todos los
 * endpoints que consume la app.
 */
const API_PUBLICA: string = `${environment.apiUrl}/public`;

export const API_RUTAS: Readonly<{
  farmaciasAprobadas: string;
  buscarMedicamentos: string;
}> = {
  /** GET: farmacias aprobadas para el mapa de la landing */
  farmaciasAprobadas: `${API_PUBLICA}/farmacias/aprobadas`,
  /** GET ?q=: medicamentos por nombre comercial o principio activo */
  buscarMedicamentos: `${API_PUBLICA}/medicamentos/buscar`,
};
