import {
  ChangeDetectionStrategy,
  Component,
  WritableSignal,
  signal,
} from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING, SeccionLanding } from "../../landing.secciones";

/**
 * Encabezado de la landing (capa features, presentación).
 *
 * Qué es: la barra superior fija (sticky) con el logotipo, la navegación entre
 * secciones y los accesos a iniciar sesión y registrarse.
 *
 * Cómo funciona: arma los enlaces a partir de SECCIONES_LANDING. Por debajo del
 * breakpoint de escritorio el menú se pliega, y la signal menuAbierto lo abre y
 * lo cierra.
 *
 * Para qué sirve: cumple el criterio de la HU-10 de tener el inicio de sesión y
 * el registro accesibles desde cualquier punto de la página.
 */
@Component({
  selector: "app-encabezado",
  imports: [RouterLink],
  templateUrl: "./encabezado.component.html",
  styleUrl: "./encabezado.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncabezadoComponent {
  protected readonly secciones: readonly SeccionLanding[] = SECCIONES_LANDING;

  /** Solo se usa por debajo del breakpoint de escritorio, donde el menú se pliega */
  protected readonly menuAbierto: WritableSignal<boolean> = signal(false);

  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }
}
