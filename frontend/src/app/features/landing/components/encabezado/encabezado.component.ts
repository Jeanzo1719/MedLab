import { ChangeDetectionStrategy, Component, signal } from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING } from "../../landing.secciones";

@Component({
  selector: "app-encabezado",
  imports: [RouterLink],
  templateUrl: "./encabezado.component.html",
  styleUrl: "./encabezado.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncabezadoComponent {
  protected readonly secciones = SECCIONES_LANDING;

  /** Only used below the desktop breakpoint, where the menu collapses */
  protected readonly menuAbierto = signal(false);

  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }
}
