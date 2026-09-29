import { ChangeDetectionStrategy, Component } from "@angular/core";

import { SERVICIOS, Servicio } from "./seccion-servicios.contenido";

/**
 * Sección de servicios de la landing (capa features, presentación).
 *
 * Qué es: las cuatro tarjetas con los servicios principales de MedLab.
 *
 * Cómo funciona: recorre SERVICIOS y dibuja una tarjeta con ícono, título y
 * descripción por cada uno.
 *
 * Para qué sirve: explica al visitante qué ofrece la plataforma.
 */
@Component({
  selector: "app-seccion-servicios",
  templateUrl: "./seccion-servicios.component.html",
  styleUrl: "./seccion-servicios.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionServiciosComponent {
  protected readonly servicios: readonly Servicio[] = SERVICIOS;
}
