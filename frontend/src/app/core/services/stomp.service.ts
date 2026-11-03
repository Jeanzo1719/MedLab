import { Injectable, inject } from "@angular/core";
import { IMessage, RxStomp, StompHeaders } from "@stomp/rx-stomp";
import { Observable, defer, from, ignoreElements, map, merge, take, tap, timeout } from "rxjs";

import { environment } from "../../../environments/environment";
import { SesionService } from "./sesion.service";

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
   * la sesión del usuario, de donde sale el token que se envía en cada mensaje
   *
   */
  private readonly sesionService: SesionService = inject(SesionService);

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
    return this.cliente.watch({ destination: destino, subHeaders: () => this.conSesion(encabezados) }).pipe(
      take(1),
      timeout(TIEMPO_MAXIMO_RESPUESTA_MS),
      map((mensaje: IMessage): T => JSON.parse(mensaje.body) as T),
    );
  }

  /**
   *
   * envía un mensaje y devuelve la primera respuesta que llegue a otro destino,
   * sin perderla por carrera
   *
   * primero se suscribe al destino de respuesta y espera el RECEIPT del broker, que
   * confirma que la suscripción ya existe. Solo entonces envía el mensaje; así la
   * respuesta nunca llega antes de que alguien la escuche. Si en 10 segundos no llega
   * nada, devuelve un error
   *
   */
  enviarYEsperar<T>(destinoEnvio: string, destinoRespuesta: string, encabezados: StompHeaders = {}): Observable<T> {
    return defer((): Observable<T> => {
      const receiptId: string = crypto.randomUUID();
      const suscripcionConfirmada = this.cliente.asyncReceipt(receiptId);

      const respuesta$: Observable<T> = this.cliente
        .watch({
          destination: destinoRespuesta,
          subHeaders: (): StompHeaders => this.conSesion({ receipt: receiptId }),
        })
        .pipe(
          take(1),
          timeout(TIEMPO_MAXIMO_RESPUESTA_MS),
          map((mensaje: IMessage): T => JSON.parse(mensaje.body) as T),
        );

      const envio$: Observable<never> = from(suscripcionConfirmada).pipe(
        tap((): void => this.publicar(destinoEnvio, encabezados)),
        ignoreElements(),
      );

      return merge(respuesta$, envio$);
    });
  }

  /**
   * 
   * escucha un destino del backend y devuelve cada mensaje que llegue
   * 
   * a diferencia de consultar, no termina con la primera respuesta: sigue
   * recibiendo cada vez que el backend publica algo en ese destino (por ejemplo
   * /topic/medicamentos/5), hasta que se deje de escuchar
   * 
   * T es el tipo del dato que se espera recibir, por ejemplo MedicamentoDetalle
   * 
   */
  escuchar<T>(destino: string): Observable<T> {
    return this.cliente
      .watch({ destination: destino, subHeaders: () => this.conSesion() })
      .pipe(map((mensaje: IMessage): T => JSON.parse(mensaje.body) as T));
  }

  /**
   *
   * envía un mensaje al backend, por ejemplo una calificación, sin esperar respuesta
   *
   * los datos extra van en encabezados. Si la conexión está caída, el mensaje se
   * envía cuando vuelva
   *
   */
  publicar(destino: string, encabezados: StompHeaders = {}): void {
    this.cliente.publish({ destination: destino, headers: this.conSesion(encabezados) });
  }

  /**
   *
   * agrega el token de la sesión a los encabezados de un mensaje
   *
   * el backend lo revisa en cada mensaje que va a un destino privado. Si no hay
   * sesión, los encabezados quedan igual: los destinos públicos no lo necesitan
   *
   */
  private conSesion(encabezados: StompHeaders = {}): StompHeaders {
    const token: string | null = this.sesionService.token();
    return token ? { ...encabezados, Authorization: `Bearer ${token}` } : encabezados;
  }
}