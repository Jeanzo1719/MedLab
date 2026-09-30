import { environment } from "../../../environments/environment";

const API_PUBLICA: string = `${environment.apiUrl}/public`;

export const API_RUTAS: Readonly<{
  farmaciasAprobadas: string;
  buscarMedicamentos: string;
}> = {
  farmaciasAprobadas: `${API_PUBLICA}/farmacias/aprobadas`,
  buscarMedicamentos: `${API_PUBLICA}/medicamentos/buscar`,
};
