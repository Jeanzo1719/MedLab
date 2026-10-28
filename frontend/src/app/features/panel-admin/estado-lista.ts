import { Observable, catchError, map, of, startWith } from "rxjs";

import { Pagina, mensajeDeError } from "../../core/services/panel-farmacia.service";

/**
 * 
 * los estados en que puede estar una tabla paginada del panel, para saber qué mostrar
 * 
 */
export type EstadoLista<T> =
  | { tipo: "cargando" }
  | { tipo: "listo"; resultado: Pagina<T> }
  | { tipo: "error"; mensaje: string };

/**
 * 
 * convierte una consulta paginada en los estados por los que pasa: cargando, y después
 * listo con la página o error con el mensaje
 * 
 */
export function aEstadoLista<T>(consulta: Observable<Pagina<T>>): Observable<EstadoLista<T>> {
  return consulta.pipe(
    map((resultado): EstadoLista<T> => ({ tipo: "listo", resultado })),
    catchError((error: unknown) => of<EstadoLista<T>>({ tipo: "error", mensaje: mensajeDeError(error) })),
    startWith<EstadoLista<T>>({ tipo: "cargando" }),
  );
}