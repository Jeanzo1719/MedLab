import {
  ChangeDetectionStrategy,
  Component,
  Signal,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import {
  Observable,
  catchError,
  debounceTime,
  distinctUntilChanged,
  map,
  of,
  startWith,
  switchMap,
} from "rxjs";

import { MedicamentoBusqueda } from "../../../../core/models/medicamento-busqueda.model";
import { MedicamentoService } from "../../../../core/services/medicamento.service";

/**
 * Mismos límites que valida el backend: lo más corto no se envía y lo más
 * largo no se puede escribir
 */
const LONGITUD_MINIMA_BUSQUEDA: number = 2;
const LONGITUD_MAXIMA_BUSQUEDA: number = 150;
/** Espera a que el usuario deje de escribir en lugar de enviar una petición por tecla */
const ESPERA_ESCRITURA_MS: number = 300;

type EstadoBusqueda =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; medicamentos: MedicamentoBusqueda[] }
  | { tipo: "error" };

/**
 * Buscador de medicamentos de la landing (capa features, componente con datos).
 *
 * Qué es: el campo "Busca un medicamento" y su lista de resultados, que se usa
 * sin cuenta.
 *
 * Cómo funciona: cada tecla actualiza la signal texto. Un flujo de RxJS espera
 * 300 ms sin escribir, quita espacios, ignora textos repetidos y cancela la
 * petición anterior con switchMap. El resultado es una máquina de estados
 * (inactivo, cargando, resultados, error) que la plantilla muestra con @if.
 *
 * Para qué sirve: cumple el criterio de la HU-10 de buscar un producto sin
 * cuenta. Es el único componente de sección que pide datos por su cuenta, porque
 * su búsqueda depende de lo que escribe el usuario y no de la página.
 */
@Component({
  selector: "app-buscador-medicamentos",
  templateUrl: "./buscador-medicamentos.component.html",
  styleUrl: "./buscador-medicamentos.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuscadorMedicamentosComponent {
  private readonly medicamentoService: MedicamentoService =
    inject(MedicamentoService);

  protected readonly longitudMinima: number = LONGITUD_MINIMA_BUSQUEDA;
  protected readonly longitudMaxima: number = LONGITUD_MAXIMA_BUSQUEDA;
  protected readonly texto: WritableSignal<string> = signal("");

  // switchMap cancela la petición anterior: resultados viejos nunca pisan a los nuevos
  protected readonly estado: Signal<EstadoBusqueda> = toSignal(
    toObservable(this.texto).pipe(
      debounceTime(ESPERA_ESCRITURA_MS),
      map((texto) => texto.trim()),
      distinctUntilChanged(),
      switchMap((texto) => this.buscar(texto)),
    ),
    { initialValue: { tipo: "inactivo" } as EstadoBusqueda },
  );

  private buscar(texto: string): Observable<EstadoBusqueda> {
    if (texto.length < LONGITUD_MINIMA_BUSQUEDA) {
      return of({ tipo: "inactivo" });
    }

    return this.medicamentoService.buscar(texto).pipe(
      map((medicamentos): EstadoBusqueda => ({ tipo: "resultados", medicamentos })),
      catchError(() => of<EstadoBusqueda>({ tipo: "error" })),
      startWith<EstadoBusqueda>({ tipo: "cargando" }),
    );
  }
}
