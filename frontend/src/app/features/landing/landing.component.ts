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

/**
 * Página de la landing pública (capa features, contenedor).
 *
 * Qué es: la página que se muestra en la ruta "/" y que arma el encabezado, las
 * tres secciones y el pie de página.
 *
 * Cómo funciona: es el componente que maneja los datos. Pide las farmacias
 * aprobadas a FarmaciaService, las convierte en marcadores genéricos del mapa y
 * se las pasa a la sección de inicio. Además define la descripción SEO de la
 * página con MetadatosService.
 *
 * Para qué sirve: separa los datos de la presentación. Las secciones solo
 * muestran lo que reciben, y la página es la única que habla con los servicios.
 */
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

  // Si la API no responde, el mapa se muestra igual, solo que sin farmacias
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
