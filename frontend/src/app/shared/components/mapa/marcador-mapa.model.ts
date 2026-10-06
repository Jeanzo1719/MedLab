/**
 * 
 * la forma de un punto que se dibuja en el mapa
 * 
 * es genérica: no sabe de farmacias, así que el mapa se puede usar para
 * cualquier cosa que tenga una ubicación
 * 
 */
export interface MarcadorMapa {
  /**
   * 
   * el número que identifica al punto
   * 
   */
  id: number;

  /**
   * 
   * la posición norte-sur del punto
   * 
   */
  latitud: number;

  /**
   * 
   * la posición este-oeste del punto
   * 
   */
  longitud: number;

  /**
   * 
   * el texto que aparece al pasar el mouse o tocar el punto
   * 
   */
  etiqueta: string;
}
