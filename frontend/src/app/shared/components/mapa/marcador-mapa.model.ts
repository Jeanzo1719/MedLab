/** Generic point drawn on the map; features map their own data to this shape */
export interface MarcadorMapa {
  id: string;
  latitud: number;
  longitud: number;
  etiqueta: string;
}
