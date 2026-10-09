import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { Coordenadas } from "./ubicacion.service";

/**
 * 
 * la dirección de los servidores de rutas gratuitos de OpenStreetMap (OSRM).
 * Reciben un punto de salida y uno de llegada y devuelven el camino
 * 
 */
const URL_OSRM: string = "https://routing.openstreetmap.de";

/**
 * 
 * cómo se va hasta la farmacia: a pie o en carro
 * 
 */
export type MedioRuta = "a-pie" | "en-carro";

/**
 * 
 * el servidor de OSRM que corresponde a cada medio: routed-foot calcula rutas
 * para caminar y routed-car para ir en carro
 * 
 */
const PERFILES_OSRM: Readonly<Record<MedioRuta, string>> = {
  "a-pie": "routed-foot",
  "en-carro": "routed-car",
};

/**
 * 
 * el camino desde el usuario hasta la farmacia
 * 
 */
export interface Ruta {
  /**
   * 
   * cuánto mide el camino, en kilómetros
   * 
   */
  distanciaKm: number;

  /**
   * 
   * cuánto se tarda en recorrerlo, en minutos (es un estimado)
   * 
   */
  duracionMin: number;

  /**
   * 
   * los puntos por los que pasa el camino, en orden. El mapa los une con una línea
   * 
   */
  puntos: Coordenadas[];
}

/**
 * 
 * la parte que se usa de la respuesta de OSRM: code dice si salió bien ("Ok") y
 * routes trae las rutas encontradas, cada una con su distancia en metros, su
 * duración en segundos y sus puntos (primero la longitud y después la latitud)
 * 
 */
interface RespuestaOsrm {
  code: string;
  routes: {
    distance: number;
    duration: number;
    geometry: { coordinates: [number, number][] };
  }[];
}

/**
 * 
 * el service que traza el camino desde el usuario hasta una farmacia
 * 
 */
@Injectable({ providedIn: "root" })
export class RutaService {
  /**
   * 
   * devuelve la ruta desde el origen hasta el destino en el medio elegido, o
   * null si OSRM no encuentra un camino
   * 
   * arma la dirección con los dos puntos (OSRM los pide como longitud,latitud)
   * y se la pide a OSRM con fetch. De la respuesta toma la primera ruta, pasa
   * los metros a kilómetros y los segundos a minutos, y voltea cada punto a
   * latitud y longitud. Si se deja de escuchar antes de que llegue la respuesta
   * (por ejemplo, porque el usuario eligió otra farmacia), cancela la consulta
   * con el AbortController
   * 
   */
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
