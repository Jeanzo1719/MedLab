import { Injectable } from "@angular/core";
import { IMessage, RxStomp, StompHeaders } from "@stomp/rx-stomp";
import { Observable, map, take, timeout } from "rxjs";

import { environment } from "../../../environments/environment";

const TIEMPO_MAXIMO_RESPUESTA_MS: number = 10000;

@Injectable({ providedIn: "root" })
export class StompService {
  private readonly cliente: RxStomp = new RxStomp();

  constructor() {
    this.cliente.configure({ brokerURL: environment.wsUrl });
    this.cliente.activate();
  }

  consultar<T>(destino: string, encabezados: StompHeaders = {}): Observable<T> {
    return this.cliente.watch({ destination: destino, subHeaders: encabezados }).pipe(
      take(1),
      timeout(TIEMPO_MAXIMO_RESPUESTA_MS),
      map((mensaje: IMessage): T => JSON.parse(mensaje.body) as T),
    );
  }
}
