import {
  CircleMarkerOptions,
  LatLngBoundsExpression,
  LatLngExpression,
} from "leaflet";

export const CENTRO_INICIAL: LatLngExpression = [6.2442, -75.5812];
export const ZOOM_INICIAL: number = 13;

export const ZOOM_MINIMO: number = 3;
export const ZOOM_MAXIMO: number = 19;

export const LIMITES_MUNDO: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180],
];

export const CAPA_BASE: Readonly<{ url: string; atribucion: string }> = {
  url: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
  atribucion:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

export const ESTILO_MARCADOR: CircleMarkerOptions = {
  radius: 8,
  color: "#ffffff",
  weight: 2,
  fillColor: "#159a63",
  fillOpacity: 1,
};
