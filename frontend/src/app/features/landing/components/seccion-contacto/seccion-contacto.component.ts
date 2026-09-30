import { ChangeDetectionStrategy, Component } from "@angular/core";

import { MEDIOS_CONTACTO, MedioContacto } from "./seccion-contacto.contenido";

@Component({
  selector: "app-seccion-contacto",
  templateUrl: "./seccion-contacto.component.html",
  styleUrl: "./seccion-contacto.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionContactoComponent {
  protected readonly medios: readonly MedioContacto[] = MEDIOS_CONTACTO;
}
