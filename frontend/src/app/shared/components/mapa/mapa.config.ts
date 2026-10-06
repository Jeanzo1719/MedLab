import {
  CircleMarkerOptions,
  LatLngBoundsExpression,
  LatLngExpression,
} from "leaflet";

/**
 * 
 * el punto donde se centra el mapa al abrirse: el centro de Medellín
 * 
 */
export const CENTRO_INICIAL: LatLngExpression = [6.2442, -75.5812];

/**
 * 
 * qué tan cerca se ve el mapa al abrirse. 13 muestra la ciudad por barrios
 * 
 */
export const ZOOM_INICIAL: number = 13;

/**
 * 
 * lo más lejos que se puede alejar el mapa (3 muestra continentes)
 * 
 */
export const ZOOM_MINIMO: number = 3;

/**
 * 
 * lo más cerca que se puede acercar el mapa (19 muestra calles y casas)
 * 
 */
export const ZOOM_MAXIMO: number = 19;

/**
 * 
 * los bordes del mapa, para que no se pueda arrastrar fuera del mundo
 * 
 * va de -85 a 85 de latitud porque más allá de eso el mapa ya no tiene imágenes
 * 
 */
export const LIMITES_MUNDO: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180],
];

/**
 * 
 * de dónde salen las imágenes del mapa (calles, ríos, barrios)
 * 
 * url es la dirección de OpenStreetMap, un mapa libre y gratuito que no pide
 * clave. Las {z}, {x} y {y} las reemplaza Leaflet con el zoom y la posición de
 * cada cuadro. atribucion es el crédito a OpenStreetMap que se muestra en la
 * esquina del mapa, como piden sus condiciones de uso
 * 
 */
export const CAPA_BASE: Readonly<{ url: string; atribucion: string }> = {
  url: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
  atribucion:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

/**
 * 
 * cómo se ve cada punto del mapa: un círculo verde de la marca con borde blanco
 * 
 */
export const ESTILO_MARCADOR: CircleMarkerOptions = {
  radius: 8,
  color: "#ffffff",
  weight: 2,
  fillColor: "#159a63",
  fillOpacity: 1,
};
