export interface SeccionLanding {
  id: string;
  etiqueta: string;
}

export const SECCIONES_LANDING: readonly SeccionLanding[] = [
  { id: "inicio", etiqueta: "Inicio" },
  { id: "servicios", etiqueta: "Servicios" },
  { id: "contacto", etiqueta: "Contacto" },
];
