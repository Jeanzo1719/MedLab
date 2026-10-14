import {
  ChangeDetectionStrategy,
  Component,
  WritableSignal,
  signal,
} from "@angular/core";
import { RouterLink } from "@angular/router";

/**
 * 
 * el encabezado de la landing: el logo, el menú de secciones y los botones de
 * iniciar sesión y registrarse
 * 
 * se queda pegado arriba al bajar por la página. En celular y tablet el menú se
 * esconde detrás de un botón (☰); en computador se ve completo
 * 
 */
@Component({
  selector: "app-encabezado",
  imports: [RouterLink],
  templateUrl: "./encabezado.component.html",
  styleUrl: "./encabezado.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncabezadoComponent {
  /**
   * 
   * si el menú está abierto o cerrado en celular y tablet. Empieza cerrado
   * 
   */
  protected readonly menuAbierto: WritableSignal<boolean> = signal(false);

  /**
   * 
   * abre el menú si está cerrado y lo cierra si está abierto. Lo usa el botón ☰
   * 
   */
  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  /**
   * 
   * cierra el menú. Se usa al tocar un enlace, para que el menú no tape la
   * sección a la que se saltó
   * 
   */
  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }
}
