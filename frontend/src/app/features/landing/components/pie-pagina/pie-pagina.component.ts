import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING, SeccionLanding } from "../../landing.secciones";

/**
 * 
 * el pie de página de la landing: el logo, una frase sobre MedLab, los enlaces
 * a las secciones y los derechos reservados
 * 
 */
@Component({
  selector: "app-pie-pagina",
  imports: [RouterLink],
  templateUrl: "./pie-pagina.component.html",
  styleUrl: "./pie-pagina.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PiePaginaComponent {
  /**
   * 
   * las secciones a las que se puede saltar desde el pie, las mismas del
   * encabezado (tomadas de landing.secciones)
   * 
   */
  protected readonly secciones: readonly SeccionLanding[] = SECCIONES_LANDING;

  /**
   * 
   * el año actual, para el texto de derechos reservados. Se calcula solo, así
   * que no hay que cambiarlo cada año
   * 
   */
  protected readonly anioActual: number = new Date().getFullYear();
}
