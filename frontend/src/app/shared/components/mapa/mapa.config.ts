import {
  CircleMarkerOptions,
  LatLngBoundsExpression,
  LatLngExpression,
} from "leaflet";

/**
 * Configuración del mapa compartido (capa shared).
 *
 * Qué es: las constantes de Leaflet (centro, zooms, límites, capa base y estilo
 * de los marcadores).
 *
 * Para qué sirve: separa la configuración del componente, así ajustar la vista
 * inicial o el estilo no toca la lógica de MapaComponent. El color de los
 * marcadores es el verde de la marca (--color-marca); se repite aquí porque
 * Leaflet no lee variables CSS.
 */
export const CENTRO_INICIAL: LatLngExpression = [6.2442, -75.5812];
export const ZOOM_INICIAL: number = 13;

/** Con zoom 3 se ve el mundo entero sin que se repita */
export const ZOOM_MINIMO: number = 3;
export const ZOOM_MAXIMO: number = 19;

/** Impide arrastrar el mapa fuera del mundo, hacia el espacio gris vacío */
export const LIMITES_MUNDO: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180],
];

/** Teselas de OpenStreetMap; su licencia exige mostrar la atribución */
export const CAPA_BASE: Readonly<{ url: string; atribucion: string }> = {
  url: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
  atribucion:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

/** Marcadores vectoriales: no cargan imágenes y se ven nítidos con cualquier zoom */
export const ESTILO_MARCADOR: CircleMarkerOptions = {
  radius: 8,
  color: "#ffffff",
  weight: 2,
  fillColor: "#159a63",
  fillOpacity: 1,
};
