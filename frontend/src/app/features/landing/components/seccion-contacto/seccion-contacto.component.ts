import { ChangeDetectionStrategy, Component } from "@angular/core";

import { MEDIOS_CONTACTO, MedioContacto } from "./seccion-contacto.contenido";

/**
 * Sección de contacto de la landing (capa features, presentación).
 *
 * Qué es: el bloque con el correo, la ubicación y el horario de atención.
 *
 * Cómo funciona: recorre MEDIOS_CONTACTO y muestra cada medio con su ícono, como
 * enlace cuando tiene uno.
 *
 * Para qué sirve: cumple el criterio de la HU-10 de mostrar información de
 * contacto y ubicación.
 */
@Component({
  selector: "app-seccion-contacto",
  templateUrl: "./seccion-contacto.component.html",
  styleUrl: "./seccion-contacto.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionContactoComponent {
  protected readonly medios: readonly MedioContacto[] = MEDIOS_CONTACTO;
}
