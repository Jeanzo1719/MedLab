/**
 * 
 * la forma de una farmacia tal como llega del backend para el mapa. Es el
 * espejo de FarmaciaMapaDto: mismos campos y mismos nombres
 * 
 * no guarda datos ni hace nada, solo le dice a TypeScript qué esperar
 * 
 */
export interface FarmaciaMapa {
  /**
   * 
   * el número que identifica a la farmacia
   * 
   */
  id: number;

  /**
   * 
   * el nombre de la farmacia, el que se ve al tocar el marcador
   * 
   */
  nombre: string;

  /**
   * 
   * la posición norte-sur de la farmacia
   * 
   */
  latitud: number;

  /**
   * 
   * la posición este-oeste de la farmacia
   * 
   */
  longitud: number;
}
