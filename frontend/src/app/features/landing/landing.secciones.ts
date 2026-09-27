export interface SeccionLanding {
  /** DOM id of the section, used as the URL fragment (/#servicios) */
  id: string;
  etiqueta: string;
}

/** Single source of truth for the landing sections and their navigation */
export const SECCIONES_LANDING: readonly SeccionLanding[] = [
  { id: "inicio", etiqueta: "Inicio" },
  { id: "servicios", etiqueta: "Servicios" },
  { id: "contacto", etiqueta: "Contacto" },
];
