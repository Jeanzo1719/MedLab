/**
 * 
 * la forma de cada resultado del buscador tal como llega del backend. Es el
 * espejo de MedicamentoBusquedaDto: mismos campos y mismos nombres
 * 
 * no guarda datos ni hace nada, solo le dice a TypeScript qué esperar
 * 
 */
export interface MedicamentoBusqueda {
  /**
   * 
   * el número que identifica al medicamento
   * 
   */
  id: number;

  /**
   * 
   * el nombre de marca con el que se vende, por ejemplo "Dolex"
   * 
   */
  nombreComercial: string;

  /**
   * 
   * la sustancia del medicamento que hace efecto, por ejemplo "Acetaminofén"
   * 
   */
  principioActivo: string;

  /**
   * 
   * cómo viene empacado, por ejemplo "500 mg x 10"
   * 
   */
  presentacion: string;

  /**
   * 
   * la forma en que viene, por ejemplo "Tableta"
   * 
   */
  formaFarmaceutica: string;
}
