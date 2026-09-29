import {
  CircleMarkerOptions,
  LatLngBoundsExpression,
  LatLngExpression,
} from "leaflet";

/** Initial view: Medellín, Colombia */
export const CENTRO_INICIAL: LatLngExpression = [6.2442, -75.5812];
export const ZOOM_INICIAL: number = 13;

/** Zoom 3 keeps the whole world visible without repeating it */
export const ZOOM_MINIMO: number = 3;
export const ZOOM_MAXIMO: number = 19;

/** Prevents panning outside the world into empty grey space */
export const LIMITES_MUNDO: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180],
];

/** OpenStreetMap tiles; its license requires showing the attribution */
export const CAPA_BASE: Readonly<{ url: string; atribucion: string }> = {
  url: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
  atribucion:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

/** Vector markers: no image files to load, sharp at any zoom */
export const ESTILO_MARCADOR: CircleMarkerOptions = {
  radius: 8,
  color: "#ffffff",
  weight: 2,
  fillColor: "#159a63",
  fillOpacity: 1,
};
