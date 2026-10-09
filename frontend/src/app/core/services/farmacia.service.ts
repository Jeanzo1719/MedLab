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

/**
 * 
 * la forma de una farmacia cercana tal como llega del backend. Es el espejo de
 * FarmaciaCercanaDto
 * 
 * tiene todo lo de FarmaciaMapa (por eso extends) más la distancia, así el mapa
 * la puede dibujar igual que a las demás farmacias
 * 
 */
export interface FarmaciaCercana extends FarmaciaMapa {
  /**
   * 
   * qué tan lejos está la farmacia del usuario, en kilómetros
   * 
   */
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

  /**
   * 
   * devuelve las farmacias aprobadas más cercanas al usuario que tienen el
   * medicamento disponible, de la más cercana a la más lejana
   * 
   * se las pide al backend por WebSocket en el destino /app/farmacias/cercanas,
   * enviando el medicamento y la ubicación del usuario como datos extra. Los
   * números se envían como texto (String) porque así viajan los datos extra
   * 
   */
  listarCercanas(medicamentoId: number, latitud: number, longitud: number): Observable<FarmaciaCercana[]> {
    return this.stomp.consultar<FarmaciaCercana[]>("/app/farmacias/cercanas", {
      medicamentoId: String(medicamentoId),
      latitud: String(latitud),
      longitud: String(longitud),
    });
  }
}
