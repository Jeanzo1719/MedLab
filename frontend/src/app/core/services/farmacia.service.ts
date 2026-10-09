import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { StompService } from "./stomp.service";

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

export interface FarmaciaCercana extends FarmaciaMapa {
  distanciaKm: number;
}

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

  listarCercanas(medicamentoId: number, latitud: number, longitud: number): Observable<FarmaciaCercana[]> {
    return this.stomp.consultar<FarmaciaCercana[]>("/app/farmacias/cercanas", {
      medicamentoId: String(medicamentoId),
      latitud: String(latitud),
      longitud: String(longitud),
    });
  }
}
