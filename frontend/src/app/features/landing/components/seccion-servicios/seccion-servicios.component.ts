import { ChangeDetectionStrategy, Component } from "@angular/core";

import { SERVICIOS, Servicio } from "./seccion-servicios.contenido";

/**
 * 
 * la sección de servicios de la landing: una tarjeta por cada cosa que ofrece
 * MedLab
 * 
 */
@Component({
  selector: "app-seccion-servicios",
  templateUrl: "./seccion-servicios.component.html",
  styleUrl: "./seccion-servicios.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionServiciosComponent {
  /**
   * 
   * los servicios que se muestran, tomados de seccion-servicios.contenido
   * 
   */
  protected readonly servicios: readonly Servicio[] = SERVICIOS;
}
