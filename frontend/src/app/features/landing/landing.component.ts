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
 * 
 * la página de la landing, la primera que ve cualquier persona al entrar a
 * MedLab, sin necesidad de cuenta
 * 
 * arma la página con sus partes: encabezado, inicio, servicios, contacto y pie
 * de página. Además le pide al backend las farmacias aprobadas y se las pasa al
 * mapa de la sección de inicio
 * 
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
  /**
   * 
   * la descripción de la landing que muestran los buscadores como Google debajo
   * del título
   * 
   */
  private static readonly DESCRIPCION: string =
    "Consulta y reporta la disponibilidad de medicamentos en farmacias cercanas a ti, con búsqueda por ubicación e historial de precios. Sin necesidad de crear una cuenta.";

  /**
   * 
   * el service que pide las farmacias al backend
   * 
   */
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);

  /**
   * 
   * las farmacias aprobadas que llegan del backend
   * 
   * empieza como una lista vacía y se llena cuando llega la respuesta. Si hay un
   * error, se queda vacía y el mapa se muestra sin puntos
   * 
   */
  private readonly farmacias: Signal<FarmaciaMapa[]> = toSignal(
    this.farmaciaService
      .listarAprobadas()
      .pipe(catchError(() => of<FarmaciaMapa[]>([]))),
    { initialValue: [] },
  );

  /**
   * 
   * las farmacias convertidas en puntos para el mapa: id, latitud, longitud y
   * el nombre como etiqueta
   * 
   * es computed: se recalcula solo cada vez que cambian las farmacias
   * 
   */
  protected readonly marcadores: Signal<MarcadorMapa[]> = computed(() =>
    this.farmacias().map((farmacia) => ({
      id: farmacia.id,
      latitud: farmacia.latitud,
      longitud: farmacia.longitud,
      etiqueta: farmacia.nombre,
    })),
  );

  /**
   * 
   * pone la descripción de la landing al abrir la página
   * 
   */
  constructor() {
    inject(MetadatosService).definirDescripcion(LandingComponent.DESCRIPCION);
  }
}
