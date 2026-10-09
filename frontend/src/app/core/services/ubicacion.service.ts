import { Injectable } from "@angular/core";
import { Observable, from, map } from "rxjs";

/**
 * 
 * la dirección de Nominatim, el buscador de direcciones gratuito de
 * OpenStreetMap. Recibe una dirección y devuelve sus coordenadas
 * 
 */
const URL_NOMINATIM: string = "https://nominatim.openstreetmap.org/search";

/**
 * 
 * cuánto se espera a que el dispositivo dé la ubicación: 10 segundos
 * 
 */
const TIEMPO_MAXIMO_UBICACION_MS: number = 10000;

/**
 * 
 * cuánto tiempo sirve una ubicación ya conocida: 1 minuto. Si el dispositivo
 * tiene una más nueva que eso, la usa sin volver a buscarla
 * 
 */
const ANTIGUEDAD_MAXIMA_UBICACION_MS: number = 60000;

/**
 * 
 * un punto en el mapa: dónde está el usuario o un punto de una ruta
 * 
 */
export interface Coordenadas {
  /**
   * 
   * la posición norte-sur
   * 
   */
  latitud: number;

  /**
   * 
   * la posición este-oeste
   * 
   */
  longitud: number;
}

/**
 * 
 * la parte que se usa de cada resultado de Nominatim: la latitud (lat) y la
 * longitud (lon). Nominatim las envía como texto
 * 
 */
interface ResultadoNominatim {
  lat: string;
  lon: string;
}

/**
 * 
 * el service que consigue la ubicación del usuario, ya sea la del dispositivo
 * o la de una dirección que escriba
 * 
 */
@Injectable({ providedIn: "root" })
export class UbicacionService {
  /**
   * 
   * devuelve la ubicación del dispositivo (celular o computador)
   * 
   * se la pide al navegador, que le pregunta al usuario si da permiso. Si la da,
   * devuelve las coordenadas y termina. Si no la da, si el navegador no puede
   * ubicarse o si pasan 10 segundos, devuelve un error. enableHighAccuracy pide
   * la ubicación más precisa que se pueda (por ejemplo, el GPS del celular)
   * 
   */
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

  /**
   * 
   * devuelve las coordenadas de una dirección o ciudad que escribió el usuario,
   * o null si no la encuentra
   * 
   * se la pregunta a Nominatim con fetch. Pide solo el primer resultado
   * (limit), solo en Colombia (countrycodes) y en español (accept-language). Si
   * Nominatim responde con un error, devuelve un error
   * 
   */
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
