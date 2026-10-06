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
 * 
 * la sección de inicio de la landing, la primera que se ve: el título
 * principal, el buscador de medicamentos y el mapa de farmacias
 * 
 */
@Component({
  selector: "app-seccion-inicio",
  imports: [BuscadorMedicamentosComponent, MapaComponent],
  templateUrl: "./seccion-inicio.component.html",
  styleUrl: "./seccion-inicio.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeccionInicioComponent {
  /**
   * 
   * los puntos de las farmacias para el mapa. Los recibe de la página de la
   * landing y se los pasa al mapa
   * 
   */
  readonly marcadores: InputSignal<MarcadorMapa[]> = input<MarcadorMapa[]>([]);
}
