/**
 * 
 * la forma de cada dato de contacto que se muestra
 * 
 */
export interface MedioContacto {
  /**
   * 
   * qué tipo de dato es, por ejemplo "Correo"
   * 
   */
  titulo: string;

  /**
   * 
   * el dato en sí, por ejemplo el correo o el horario
   * 
   */
  valor: string;

  /**
   * 
   * el enlace al que lleva al tocarlo, por ejemplo mailto: para abrir el correo.
   * Es opcional (?): los datos sin enlace se muestran como texto normal
   * 
   */
  enlace?: string;

  /**
   * 
   * el dibujo del ícono, escrito como el trazo de un SVG
   * 
   */
  icono: string;
}

// TODO: reemplazar estos valores provisionales por los datos de contacto oficiales de la plataforma
/**
 * 
 * los datos de contacto de MedLab: correo, ubicación y horario
 * 
 * son provisionales hasta tener los oficiales (ver el TODO de arriba)
 * 
 */
export const MEDIOS_CONTACTO: readonly MedioContacto[] = [
  {
    titulo: "Correo",
    valor: "contacto@medlab.example",
    enlace: "mailto:contacto@medlab.example",
    icono: "M4 6h16v12H4z M4 7l8 6 8-6",
  },
  {
    titulo: "Ubicación",
    valor: "Medellín, Antioquia, Colombia",
    icono:
      "M12 21s-7-6.2-7-11a7 7 0 0 1 14 0c0 4.8-7 11-7 11z M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
  },
  {
    titulo: "Horario de atención",
    valor: "Lunes a viernes, 8:00 a. m. a 6:00 p. m.",
    icono: "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z M12 7v5l3 2",
  },
];
