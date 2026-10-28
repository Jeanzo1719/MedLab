import { ChangeDetectionStrategy, Component, Signal, inject } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { RouterLink } from "@angular/router";
import { catchError, map, of, startWith } from "rxjs";

import {
  PanelFarmaciaService,
  ResumenInventario,
  mensajeDeError,
} from "../../../core/services/panel-farmacia.service";

/**
 * 
 * los estados en que puede estar el resumen, para saber qué mostrar
 * 
 */
type EstadoResumen =
  | { tipo: "cargando" }
  | { tipo: "listo"; resumen: ResumenInventario }
  | { tipo: "error"; mensaje: string };

/**
 * 
 * el resumen del inventario: cuántos productos tiene la farmacia en cada estado
 * 
 * lo pide una sola vez al abrirse; si el inventario cambia, se actualiza al volver
 * a entrar a esta pestaña
 * 
 */
@Component({
  selector: "app-resumen-farmacia",
  imports: [RouterLink],
  templateUrl: "./resumen-farmacia.component.html",
  styleUrl: "./resumen-farmacia.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResumenFarmaciaComponent {
  private readonly panel: PanelFarmaciaService = inject(PanelFarmaciaService);

  protected readonly estado: Signal<EstadoResumen> = toSignal(
    this.panel.obtenerResumen().pipe(
      map((resumen): EstadoResumen => ({ tipo: "listo", resumen })),
      catchError((error: unknown) => of<EstadoResumen>({ tipo: "error", mensaje: mensajeDeError(error) })),
      startWith<EstadoResumen>({ tipo: "cargando" }),
    ),
    { initialValue: { tipo: "cargando" } as EstadoResumen },
  );
}