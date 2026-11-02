import { Injectable, inject } from "@angular/core";
import { EMPTY, Observable, defer, map, merge } from "rxjs";

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
 * tiene todo lo de FarmaciaMapa (por eso extends) más la distancia y el número
 * de su reporte de disponibilidad, así el mapa la puede dibujar igual que a las
 * demás farmacias
 * 
 */
export interface FarmaciaCercana extends FarmaciaMapa {
  /**
   * 
   * qué tan lejos está la farmacia del usuario, en kilómetros
   * 
   */
  distanciaKm: number;

  /**
   * 
   * el número del reporte de disponibilidad de la farmacia para el medicamento
   * buscado. Con él se encuentra su mensaje de disponibilidad
   * 
   */
  disponibilidadId: number;
}

/**
 *
 * una farmacia que espera aprobación, con los datos que revisa el administrador.
 * Es el espejo de FarmaciaPendienteDto
 *
 */
export interface FarmaciaPendiente {
  id: number;
  nombre: string;
  nit: string;
  rut: string;
  matriculaMercantil: string;
  identificacionDirectorTecnico: string;
  licenciaSanitaria: string;
}

/**
 *
 * el destino donde el backend publica la lista de farmacias pendientes cada vez
 * que una se aprueba o se rechaza
 *
 */
const DESTINO_CAMBIOS_PENDIENTES: string = "/topic/farmacias/pendientes";

/**
 * 
 * el service de las farmacias en el frontend. Le pide al backend las farmacias
 * que se muestran en el mapa y las solicitudes que revisa el administrador
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

  /**
   *
   * devuelve las farmacias pendientes y cada actualización de la lista
   *
   * es solo para administradores: sin esa sesión el backend no responde y, a los
   * 10 segundos, devuelve un error
   *
   */
  seguirPendientes(): Observable<FarmaciaPendiente[]> {
    return merge(
      this.stomp.consultar<FarmaciaPendiente[]>("/app/farmacias/pendientes"),
      this.stomp.escuchar<FarmaciaPendiente[]>(DESTINO_CAMBIOS_PENDIENTES),
    );
  }

  /**
   *
   * aprueba una farmacia pendiente
   *
   */
  aprobar(farmaciaId: number): Observable<void> {
    return this.enviarDecision("/app/farmacias/aprobar", farmaciaId);
  }

  /**
   *
   * rechaza una farmacia pendiente
   *
   */
  rechazar(farmaciaId: number): Observable<void> {
    return this.enviarDecision("/app/farmacias/rechazar", farmaciaId);
  }

  /**
   *
   * envía la decisión del administrador y espera la confirmación
   *
   * primero escucha la lista de pendientes y después envía el mensaje. El backend
   * publica la lista al guardar, así que ese mensaje es la confirmación. Si en 10
   * segundos no llega nada, devuelve un error
   *
   */
  private enviarDecision(destino: string, farmaciaId: number): Observable<void> {
    return merge(
      this.stomp
        .consultar<FarmaciaPendiente[]>(DESTINO_CAMBIOS_PENDIENTES)
        .pipe(map((): void => undefined)),
      defer((): Observable<never> => {
        this.stomp.publicar(destino, { farmaciaId: String(farmaciaId) });
        return EMPTY;
      }),
    );
  }
}
