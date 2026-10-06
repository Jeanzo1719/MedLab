/**
 * 
 * la forma de una sección de la landing a la que se puede saltar desde el menú
 * 
 */
export interface SeccionLanding {
  /**
   * 
   * el id de la sección en el HTML. El menú lo usa para saltar a ella (#inicio)
   * 
   */
  id: string;

  /**
   * 
   * el texto que se ve en el menú, por ejemplo "Inicio"
   * 
   */
  etiqueta: string;
}

/**
 * 
 * las secciones de la landing en el orden en que aparecen: inicio, servicios
 * y contacto
 * 
 * el encabezado y el pie de página arman sus menús con esta lista, así los dos
 * siempre muestran las mismas secciones
 * 
 */
export const SECCIONES_LANDING: readonly SeccionLanding[] = [
  { id: "inicio", etiqueta: "Inicio" },
  { id: "servicios", etiqueta: "Servicios" },
  { id: "contacto", etiqueta: "Contacto" },
];
