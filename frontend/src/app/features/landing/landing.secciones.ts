/**
 * Secciones de la landing (capa features, contenido).
 *
 * Qué es: la lista de secciones (inicio, servicios, contacto) con su id y su
 * etiqueta.
 *
 * Para qué sirve: el encabezado y el pie de página construyen su navegación a
 * partir de esta lista, así agregar o renombrar una sección se hace en un solo
 * lugar.
 */
export interface SeccionLanding {
  /** id de la sección en el DOM; se usa como fragmento de la URL (/#servicios) */
  id: string;
  etiqueta: string;
}

/** Fuente única de las secciones de la landing y de su navegación */
export const SECCIONES_LANDING: readonly SeccionLanding[] = [
  { id: "inicio", etiqueta: "Inicio" },
  { id: "servicios", etiqueta: "Servicios" },
  { id: "contacto", etiqueta: "Contacto" },
];
