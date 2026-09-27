import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink } from "@angular/router";

import { SECCIONES_LANDING } from "../../landing.secciones";

@Component({
  selector: "app-pie-pagina",
  imports: [RouterLink],
  templateUrl: "./pie-pagina.component.html",
  styleUrl: "./pie-pagina.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PiePaginaComponent {
  protected readonly secciones = SECCIONES_LANDING;
  protected readonly anioActual = new Date().getFullYear();
}
