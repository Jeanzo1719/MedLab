import {
  ChangeDetectionStrategy,
  Component,
  InputSignal,
  input,
} from "@angular/core";

import { MapaComponent } from "../../../../shared/components/mapa/mapa.component";
import { MarcadorMapa } from "../../../../shared/components/mapa/marcador-mapa.model";
import { BuscadorMedicamentosComponent } from "../buscador-medicamentos/buscador-medicamentos.component";

/**
 * Sección de inicio de la landing (capa features, presentación).
 *
 * Qué es: el primer bloque de la página, con el mensaje principal (el único
 * <h1>), el buscador de medicamentos y el mapa de farmacias.
 *
 * Cómo funciona: es de presentación: recibe los marcadores como input desde
 * LandingComponent y se los pasa al mapa compartido. En escritorio muestra el
 * texto y el mapa en dos columnas.
 *
 * Para qué sirve: es lo primero que ve el visitante y reúne las dos acciones
 * principales: buscar y ubicar.
 */
@Component({
  selector: "app-seccion-inicio",
  imports: [BuscadorMedicamentosComponent, MapaComponent],
  templateUrl: "./seccion-inicio.component.html",
  styleUrl: "./seccion-inicio.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionInicioComponent {
  readonly marcadores: InputSignal<MarcadorMapa[]> = input<MarcadorMapa[]>([]);
}
