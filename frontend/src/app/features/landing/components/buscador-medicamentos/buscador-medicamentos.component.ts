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

import {
  MedicamentoBusqueda,
  MedicamentoService,
} from "../../../../core/services/medicamento.service";

/**
 * 
 * la cantidad mínima de letras para empezar a buscar (2)
 * 
 */
const LONGITUD_MINIMA_BUSQUEDA: number = 2;
/**
 * 
 * la cantidad máxima de letras que se pueden escribir (150)
 * 
 */
const LONGITUD_MAXIMA_BUSQUEDA: number = 150;
/**
 * 
 * cuánto se espera después de la última tecla antes de buscar: 300 milisegundos
 * 
 * así no se envía una búsqueda por cada letra, sino cuando el usuario hace una pausa
 * 
 */
const ESPERA_ESCRITURA_MS: number = 300;

/**
 * 
 * los estados en que puede estar el buscador, para saber qué mostrar
 * 
 * inactivo: no hay nada que buscar todavía (menos de 2 letras)
 * cargando: la búsqueda se está haciendo
 * resultados: llegó la respuesta, con la lista de medicamentos (puede estar vacía)
 * error: la búsqueda falló o el backend no respondió
 * 
 */
type EstadoBusqueda =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; medicamentos: MedicamentoBusqueda[] }
  | { tipo: "error" };

/**
 * 
 * el buscador de medicamentos de la landing. Busca mientras el usuario escribe,
 * sin tener que presionar ningún botón ni tener cuenta
 * 
 * muestra "Buscando…" mientras espera, la lista de resultados cuando llega, o un
 * mensaje si no hay resultados o si hubo un error
 * 
 */
@Component({
  selector: "app-buscador-medicamentos",
  templateUrl: "./buscador-medicamentos.component.html",
  styleUrl: "./buscador-medicamentos.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuscadorMedicamentosComponent {
  /**
   * 
   * el service que le pide los medicamentos al backend
   * 
   */
  private readonly medicamentoService: MedicamentoService =
    inject(MedicamentoService);

  /**
   * 
   * la cantidad mínima de letras, para usarla en el HTML
   * 
   */
  protected readonly longitudMinima: number = LONGITUD_MINIMA_BUSQUEDA;
  /**
   * 
   * la cantidad máxima de letras, para limitar el campo en el HTML
   * 
   */
  protected readonly longitudMaxima: number = LONGITUD_MAXIMA_BUSQUEDA;
  /**
   * 
   * el texto que el usuario va escribiendo en el campo. Empieza vacío
   * 
   */
  protected readonly texto: WritableSignal<string> = signal("");

  /**
   * 
   * el estado actual del buscador, que el HTML usa para saber qué mostrar
   * 
   * cada vez que cambia el texto: espera la pausa de 300 milisegundos, le quita
   * los espacios de los lados, ignora el cambio si el texto quedó igual y busca.
   * Si el usuario sigue escribiendo, la búsqueda anterior se cancela y solo vale
   * la última (eso hace switchMap). Empieza en inactivo
   * 
   */
  protected readonly estado: Signal<EstadoBusqueda> = toSignal(
    toObservable(this.texto).pipe(
      debounceTime(ESPERA_ESCRITURA_MS),
      map((texto) => texto.trim()),
      distinctUntilChanged(),
      switchMap((texto) => this.buscar(texto)),
    ),
    { initialValue: { tipo: "inactivo" } as EstadoBusqueda },
  );

  /**
   * 
   * hace una búsqueda y devuelve los estados por los que pasa
   * 
   * si el texto tiene menos de 2 letras, no busca y queda inactivo. Si no, primero
   * devuelve cargando y después resultados con la lista, o error si algo falló
   * 
   */
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
