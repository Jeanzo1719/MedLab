import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { MedicamentoBusqueda } from "../models/medicamento-busqueda.model";
import { StompService } from "./stomp.service";

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
