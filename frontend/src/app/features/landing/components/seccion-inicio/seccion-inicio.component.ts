import {
  ChangeDetectionStrategy,
  Component,
  InputSignal,
  input,
} from "@angular/core";

import { MapaComponent } from "../../../../shared/components/mapa/mapa.component";
import { MarcadorMapa } from "../../../../shared/components/mapa/marcador-mapa.model";
import { BuscadorMedicamentosComponent } from "../buscador-medicamentos/buscador-medicamentos.component";

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
