import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { FarmaciaMapa } from "../models/farmacia-mapa.model";
import { StompService } from "./stomp.service";

/**
 * 
 * el service de las farmacias en el frontend. Le pide al backend las farmacias
 * que se muestran en el mapa
 * 
 */
@Injectable({ providedIn: "root" })
export class FarmaciaService {
  /**
   * 
   * la conexión WebSocket con el backend
   * 
   */
  private readonly stomp: StompService = inject(StompService);

  /**
   * 
   * devuelve la lista de farmacias aprobadas para el mapa
   * 
   * se la pide al backend por WebSocket en el destino /app/farmacias/aprobadas
   * 
   */
  listarAprobadas(): Observable<FarmaciaMapa[]> {
    return this.stomp.consultar<FarmaciaMapa[]>("/app/farmacias/aprobadas");
  }
}
