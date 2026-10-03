import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING, SeccionLanding } from "../../landing.secciones";

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
