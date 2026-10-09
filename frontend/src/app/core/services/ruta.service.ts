import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { Coordenadas } from "./ubicacion.service";

const URL_OSRM: string = "https://routing.openstreetmap.de";

export type MedioRuta = "a-pie" | "en-carro";

const PERFILES_OSRM: Readonly<Record<MedioRuta, string>> = {
  "a-pie": "routed-foot",
  "en-carro": "routed-car",
};

export interface Ruta {
  distanciaKm: number;
  duracionMin: number;
  puntos: Coordenadas[];
}

interface RespuestaOsrm {
  code: string;
  routes: {
    distance: number;
    duration: number;
    geometry: { coordinates: [number, number][] };
  }[];
}

@Injectable({ providedIn: "root" })
export class RutaService {
  trazar(origen: Coordenadas, destino: Coordenadas, medio: MedioRuta): Observable<Ruta | null> {
    const tramo: string = `${origen.longitud},${origen.latitud};${destino.longitud},${destino.latitud}`;
    const url: string = `${URL_OSRM}/${PERFILES_OSRM[medio]}/route/v1/driving/${tramo}?overview=full&geometries=geojson`;

    return new Observable<Ruta | null>((suscriptor) => {
      const cancelador: AbortController = new AbortController();

      fetch(url, { signal: cancelador.signal })
        .then((respuesta: Response): Promise<RespuestaOsrm> => {
          if (!respuesta.ok) {
            throw new Error(`OSRM respondió ${respuesta.status}`);
          }
          return respuesta.json();
        })
        .then((respuesta: RespuestaOsrm): void => {
          const ruta: RespuestaOsrm["routes"][number] | undefined = respuesta.routes?.[0];
          suscriptor.next(
            respuesta.code !== "Ok" || !ruta
              ? null
              : {
                  distanciaKm: ruta.distance / 1000,
                  duracionMin: ruta.duration / 60,
                  puntos: ruta.geometry.coordinates.map(
                    ([longitud, latitud]: [number, number]): Coordenadas => ({ latitud, longitud }),
                  ),
                },
          );
          suscriptor.complete();
        })
        .catch((error: unknown): void => {
          if (!cancelador.signal.aborted) {
            suscriptor.error(error);
          }
        });

      return (): void => cancelador.abort();
    });
  }
}
