/**
 * Modelo de un medicamento en los resultados de búsqueda (capa core, modelos).
 *
 * Qué es: la forma exacta del JSON que devuelve GET
 * /api/public/medicamentos/buscar; replica el MedicamentoBusquedaDto del
 * backend.
 *
 * Para qué sirve: da tipos a la respuesta HTTP en MedicamentoService y en el
 * buscador de la landing.
 */
export interface MedicamentoBusqueda {
  id: string;
  nombreComercial: string;
  principioActivo: string;
  presentacion: string;
  formaFarmaceutica: string;
}
