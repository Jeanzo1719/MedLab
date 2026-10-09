import { Injectable } from "@angular/core";
import { Observable, from, map } from "rxjs";

const URL_NOMINATIM: string = "https://nominatim.openstreetmap.org/search";

const TIEMPO_MAXIMO_UBICACION_MS: number = 10000;

const ANTIGUEDAD_MAXIMA_UBICACION_MS: number = 60000;

export interface Coordenadas {
  latitud: number;
  longitud: number;
}

interface ResultadoNominatim {
  lat: string;
  lon: string;
}

@Injectable({ providedIn: "root" })
export class UbicacionService {
  obtenerDelDispositivo(): Observable<Coordenadas> {
    return new Observable<Coordenadas>((suscriptor) => {
      if (!("geolocation" in navigator)) {
        suscriptor.error(new Error("Geolocalización no disponible"));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (posicion: GeolocationPosition): void => {
          suscriptor.next({
            latitud: posicion.coords.latitude,
            longitud: posicion.coords.longitude,
          });
          suscriptor.complete();
        },
        (error: GeolocationPositionError): void => suscriptor.error(error),
        {
          enableHighAccuracy: true,
          timeout: TIEMPO_MAXIMO_UBICACION_MS,
          maximumAge: ANTIGUEDAD_MAXIMA_UBICACION_MS,
        },
      );
    });
  }

  buscarDireccion(direccion: string): Observable<Coordenadas | null> {
    const parametros: URLSearchParams = new URLSearchParams({
      q: direccion,
      format: "jsonv2",
      limit: "1",
      countrycodes: "co",
      "accept-language": "es",
    });

    return from(
      fetch(`${URL_NOMINATIM}?${parametros}`).then((respuesta: Response): Promise<ResultadoNominatim[]> => {
        if (!respuesta.ok) {
          throw new Error(`Nominatim respondió ${respuesta.status}`);
        }
        return respuesta.json();
      }),
    ).pipe(
      map((resultados: ResultadoNominatim[]): Coordenadas | null =>
        resultados.length === 0
          ? null
          : { latitud: Number(resultados[0].lat), longitud: Number(resultados[0].lon) },
      ),
    );
  }
}
