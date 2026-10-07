import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { StompService } from "./stomp.service";

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

/**
 * 
 * el service de los medicamentos en el frontend. Le pide al backend los
 * resultados del buscador
 * 
 */
@Injectable({ providedIn: "root" })
export class MedicamentoService {
  /**
   * 
   * la conexión WebSocket con el backend
   * 
   */
  private readonly stomp: StompService = inject(StompService);

  /**
   * 
   * devuelve los medicamentos cuyo nombre comercial o principio activo contiene
   * el texto que escribió el usuario
   * 
   * se lo pide al backend por WebSocket en el destino /app/medicamentos/buscar,
   * enviando el texto como un dato extra llamado q
   * 
   */
  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    return this.stomp.consultar<MedicamentoBusqueda[]>("/app/medicamentos/buscar", { q: texto });
  }
}
