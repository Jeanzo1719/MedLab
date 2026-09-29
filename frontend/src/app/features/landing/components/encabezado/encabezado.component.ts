import {
  ChangeDetectionStrategy,
  Component,
  WritableSignal,
  signal,
} from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING, SeccionLanding } from "../../landing.secciones";

@Component({
  selector: "app-encabezado",
  imports: [RouterLink],
  templateUrl: "./encabezado.component.html",
  styleUrl: "./encabezado.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncabezadoComponent {
  protected readonly secciones: readonly SeccionLanding[] = SECCIONES_LANDING;

  /** Only used below the desktop breakpoint, where the menu collapses */
  protected readonly menuAbierto: WritableSignal<boolean> = signal(false);

  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }
}
