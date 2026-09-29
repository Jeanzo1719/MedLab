import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  Signal,
} from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { catchError, of } from "rxjs";

import { FarmaciaMapa } from "../../core/models/farmacia-mapa.model";
import { FarmaciaService } from "../../core/services/farmacia.service";
import { MetadatosService } from "../../core/services/metadatos.service";
import { MarcadorMapa } from "../../shared/components/mapa/marcador-mapa.model";
import { EncabezadoComponent } from "./components/encabezado/encabezado.component";
import { PiePaginaComponent } from "./components/pie-pagina/pie-pagina.component";
import { SeccionContactoComponent } from "./components/seccion-contacto/seccion-contacto.component";
import { SeccionInicioComponent } from "./components/seccion-inicio/seccion-inicio.component";
import { SeccionServiciosComponent } from "./components/seccion-servicios/seccion-servicios.component";

@Component({
  selector: "app-landing",
  imports: [
    EncabezadoComponent,
    SeccionInicioComponent,
    SeccionServiciosComponent,
    SeccionContactoComponent,
    PiePaginaComponent,
  ],
  templateUrl: "./landing.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingComponent {
  private static readonly DESCRIPCION: string =
    "Consulta y reporta la disponibilidad de medicamentos en farmacias cercanas a ti, con búsqueda por ubicación e historial de precios. Sin necesidad de crear una cuenta.";

  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);

  // If the API is down the map still renders, just without pharmacies
  private readonly farmacias: Signal<FarmaciaMapa[]> = toSignal(
    this.farmaciaService
      .listarAprobadas()
      .pipe(catchError(() => of<FarmaciaMapa[]>([]))),
    { initialValue: [] },
  );

  protected readonly marcadores: Signal<MarcadorMapa[]> = computed(() =>
    this.farmacias().map((farmacia) => ({
      id: farmacia.id,
      latitud: farmacia.latitud,
      longitud: farmacia.longitud,
      etiqueta: farmacia.nombre,
    })),
  );

  constructor() {
    inject(MetadatosService).definirDescripcion(LandingComponent.DESCRIPCION);
  }
}
