/**
 * Modelo de un marcador del mapa (capa shared, modelos).
 *
 * Qué es: un punto genérico que dibuja MapaComponent: posición, identificador y
 * etiqueta del tooltip.
 *
 * Para qué sirve: mantiene al mapa independiente del dominio. Cada feature
 * convierte sus propios datos a esta forma (por ejemplo, LandingComponent
 * convierte farmacias).
 */
export interface MarcadorMapa {
  id: string;
  latitud: number;
  longitud: number;
  etiqueta: string;
}
