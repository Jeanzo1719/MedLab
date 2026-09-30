export interface Servicio {
  titulo: string;
  descripcion: string;
  icono: string;
}

export const SERVICIOS: readonly Servicio[] = [
  {
    titulo: "Búsqueda por ubicación",
    descripcion:
      "Encuentra el medicamento que necesitas en las farmacias más cercanas a ti, directamente sobre el mapa.",
    icono:
      "M12 21s-7-6.2-7-11a7 7 0 0 1 14 0c0 4.8-7 11-7 11z M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
  },
  {
    titulo: "Datos confiables",
    descripcion:
      "Los reportes de la comunidad se califican para que sepas qué tan actualizada y segura es cada disponibilidad.",
    icono: "M12 3l7 3v6c0 4.4-3 7.7-7 9-4-1.3-7-4.6-7-9V6l7-3z M9 12l2 2 4-4",
  },
  {
    titulo: "Historial de precios",
    descripcion:
      "Compara precios entre farmacias y revisa cómo han cambiado con el tiempo antes de comprar.",
    icono: "M4 19h16 M7 15l4-4 3 3 5-6",
  },
  {
    titulo: "Farmacias aliadas",
    descripcion:
      "Las farmacias verificadas actualizan su inventario desde su propio panel para mantener la información al día.",
    icono: "M4 9l1.5-5h13L20 9 M4 9h16v11H4z M10 20v-5h4v5",
  },
];
