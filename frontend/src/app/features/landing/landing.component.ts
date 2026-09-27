import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
} from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { catchError, of } from "rxjs";

import { FarmaciaService } from "../../core/services/farmacia.service";
import { MarcadorMapa } from "../../shared/components/mapa/marcador-mapa.model";
import { EncabezadoComponent } from "./components/encabezado/encabezado.component";
import { SeccionInicioComponent } from "./components/seccion-inicio/seccion-inicio.component";

@Component({
  selector: "app-landing",
  imports: [EncabezadoComponent, SeccionInicioComponent],
  templateUrl: "./landing.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingComponent {
  private readonly farmaciaService = inject(FarmaciaService);

  // If the API is down the map still renders, just without pharmacies
  private readonly farmacias = toSignal(
    this.farmaciaService.listarAprobadas().pipe(catchError(() => of([]))),
    { initialValue: [] },
  );

  protected readonly marcadores = computed<MarcadorMapa[]>(() =>
    this.farmacias().map((farmacia) => ({
      id: farmacia.id,
      latitud: farmacia.latitud,
      longitud: farmacia.longitud,
      etiqueta: farmacia.nombre,
    })),
  );
}
