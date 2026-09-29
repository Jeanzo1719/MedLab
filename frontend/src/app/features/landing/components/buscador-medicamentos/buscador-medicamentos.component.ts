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

/** Same limits the backend enforces; shorter queries are not sent and longer ones cannot be typed */
const LONGITUD_MINIMA_BUSQUEDA: number = 2;
const LONGITUD_MAXIMA_BUSQUEDA: number = 150;
/** Waits for the user to stop typing instead of sending one request per key */
const ESPERA_ESCRITURA_MS: number = 300;

type EstadoBusqueda =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; medicamentos: MedicamentoBusqueda[] }
  | { tipo: "error" };

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

  // switchMap cancels the previous request, so stale results never overwrite new ones
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
