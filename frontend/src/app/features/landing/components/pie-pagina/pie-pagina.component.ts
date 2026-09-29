import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING, SeccionLanding } from "../../landing.secciones";

/**
 * Pie de página de la landing (capa features, presentación).
 *
 * Qué es: la franja oscura final con la marca, el lema, los enlaces a las
 * secciones y los derechos reservados.
 *
 * Cómo funciona: arma los enlaces a partir de SECCIONES_LANDING y calcula el año
 * actual, así nunca queda desactualizado.
 *
 * Para qué sirve: cierra la página y repite la navegación para quien llega al
 * final.
 */
@Component({
  selector: "app-pie-pagina",
  imports: [RouterLink],
  templateUrl: "./pie-pagina.component.html",
  styleUrl: "./pie-pagina.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PiePaginaComponent {
  protected readonly secciones: readonly SeccionLanding[] = SECCIONES_LANDING;
  protected readonly anioActual: number = new Date().getFullYear();
}
