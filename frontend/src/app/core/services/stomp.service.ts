import { Injectable } from "@angular/core";
import { IMessage, RxStomp, StompHeaders } from "@stomp/rx-stomp";
import { Observable, map, take, timeout } from "rxjs";

import { environment } from "../../../environments/environment";

/**
 * 
 * el tiempo máximo que se espera una respuesta del backend: 10 segundos
 * (10000 milisegundos)
 * 
 */
const TIEMPO_MAXIMO_RESPUESTA_MS: number = 10000;

/**
 * 
 * la conexión WebSocket del frontend con el backend. Es una sola para toda
 * la app y todos los services la usan para pedir datos
 * 
 * usa la librería rx-stomp, que habla STOMP por el WebSocket y entrega las
 * respuestas como observables de RxJS. Si la conexión se cae, vuelve a
 * conectarse sola
 * 
 */
@Injectable({ providedIn: "root" })
export class StompService {
  /**
   * 
   * el cliente de rx-stomp, el que mantiene la conexión abierta con el backend
   * 
   */
  private readonly cliente: RxStomp = new RxStomp();

  /**
   * 
   * configura la dirección del WebSocket (wsUrl de environment) y abre la conexión
   * 
   * se ejecuta una sola vez, la primera vez que algún service pide la conexión
   * 
   */
  constructor() {
    this.cliente.configure({ brokerURL: environment.wsUrl });
    this.cliente.activate();
  }

  /**
   * 
   * pide un dato al backend y devuelve su respuesta
   * 
   * se suscribe al destino (por ejemplo /app/farmacias/aprobadas) con los datos
   * extra que se le pasen en encabezados, toma la primera respuesta, la convierte
   * de JSON a objeto y termina. Si en 10 segundos no llega nada, devuelve un error
   * 
   * T es el tipo del dato que se espera recibir, por ejemplo FarmaciaMapa[]
   * 
   */
  consultar<T>(destino: string, encabezados: StompHeaders = {}): Observable<T> {
    return this.cliente.watch({ destination: destino, subHeaders: encabezados }).pipe(
      take(1),
      timeout(TIEMPO_MAXIMO_RESPUESTA_MS),
      map((mensaje: IMessage): T => JSON.parse(mensaje.body) as T),
    );
  }
}
