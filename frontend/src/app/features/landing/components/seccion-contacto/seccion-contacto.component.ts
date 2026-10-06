import { ChangeDetectionStrategy, Component } from "@angular/core";

import { MEDIOS_CONTACTO, MedioContacto } from "./seccion-contacto.contenido";

/**
 * 
 * la sección de contacto de la landing: los medios para comunicarse con MedLab
 * 
 */
@Component({
  selector: "app-seccion-contacto",
  templateUrl: "./seccion-contacto.component.html",
  styleUrl: "./seccion-contacto.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionContactoComponent {
  /**
   * 
   * los datos de contacto que se muestran, tomados de seccion-contacto.contenido
   * 
   */
  protected readonly medios: readonly MedioContacto[] = MEDIOS_CONTACTO;
}
