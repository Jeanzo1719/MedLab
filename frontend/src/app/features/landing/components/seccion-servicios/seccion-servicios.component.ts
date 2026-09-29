import { ChangeDetectionStrategy, Component } from "@angular/core";

import { SERVICIOS, Servicio } from "./seccion-servicios.contenido";

@Component({
  selector: "app-seccion-servicios",
  templateUrl: "./seccion-servicios.component.html",
  styleUrl: "./seccion-servicios.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionServiciosComponent {
  protected readonly servicios: readonly Servicio[] = SERVICIOS;
}
