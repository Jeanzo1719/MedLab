/**
 * Modelo de una farmacia del mapa (capa core, modelos).
 *
 * Qué es: la forma exacta del JSON que devuelve GET
 * /api/public/farmacias/aprobadas; replica el FarmaciaMapaDto del backend.
 *
 * Para qué sirve: da tipos a la respuesta HTTP en FarmaciaService, así un cambio
 * en el contrato con el backend se detecta al compilar.
 */
export interface FarmaciaMapa {
  id: string;
  nombre: string;
  latitud: number;
  longitud: number;
}
