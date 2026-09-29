import { environment } from "../../../environments/environment";

/**
 * Backend endpoints used by the frontend, in one place.
 *
 * What it is for: services never build URLs by hand, so when the backend changes
 * a path (see RutasApi.java) only this file changes, and every endpoint the app
 * calls can be reviewed at a glance.
 *
 * How it works: each entry joins the environment's API base URL
 * (environment.apiUrl) with the path of the backend's public area.
 */
const API_PUBLICA: string = `${environment.apiUrl}/public`;

export const API_RUTAS: Readonly<{
  farmaciasAprobadas: string;
  buscarMedicamentos: string;
}> = {
  /** GET: approved pharmacies for the landing map */
  farmaciasAprobadas: `${API_PUBLICA}/farmacias/aprobadas`,
  /** GET ?q=: medicines by commercial name or active ingredient */
  buscarMedicamentos: `${API_PUBLICA}/medicamentos/buscar`,
};
