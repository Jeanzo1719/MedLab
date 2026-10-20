import { ChangeDetectionStrategy, Component, inject, Signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { RouterLink } from "@angular/router";
import { catchError, of } from "rxjs";

import { FarmaciaMapa, FarmaciaService } from "../../core/services/farmacia.service";
import { MetadatosService } from "../../core/services/metadatos.service";
import { EncabezadoComponent } from "../../shared/components/encabezado/encabezado.component";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";
import { BuscadorMedicamentosComponent } from "./components/buscador-medicamentos/buscador-medicamentos.component";
import { ComoFuncionaComponent } from "./components/como-funciona/como-funciona.component";
import { ParaFarmaciasComponent } from "./components/para-farmacias/para-farmacias.component";

/** Tarjeta de servicio. `icono` es el trazo de un SVG. */
interface Servicio {
  titulo: string;
  descripcion: string;
  icono: string;
}

/** Dato de contacto. Si tiene `enlace` se muestra como enlace. */
interface MedioContacto {
  titulo: string;
  valor: string;
  enlace?: string;
  icono: string;
}

/**
 * Landing pública (HU-10): encabezado, inicio, servicios, cómo funciona,
 * para farmacias, contacto y pie. Pide las farmacias aprobadas para el mapa.
 */
@Component({
  selector: "app-landing",
  imports: [
    EncabezadoComponent,
    BuscadorMedicamentosComponent,
    MapaComponent,
    RouterLink,
    ComoFuncionaComponent,
    ParaFarmaciasComponent,
  ],
  templateUrl: "./landing.component.html",
  styleUrl: "./landing.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingComponent {
  /** Descripción que muestran los buscadores como Google. */
  private static readonly DESCRIPCION: string =
    "Consulta y reporta la disponibilidad de medicamentos en farmacias cercanas a ti, con búsqueda por ubicación e historial de precios. Sin necesidad de crear una cuenta.";

  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);

  /** Farmacias aprobadas para el mapa. Si el backend falla, queda vacía. */
  protected readonly farmacias: Signal<FarmaciaMapa[]> = toSignal(
    this.farmaciaService
      .listarAprobadas()
      .pipe(catchError(() => of<FarmaciaMapa[]>([]))),
    { initialValue: [] },
  );

  /** Los cuatro servicios principales, en orden. */
  protected readonly servicios: readonly Servicio[] = [
    {
      titulo: "Búsqueda por ubicación",
      descripcion:
        "Encuentra el medicamento que necesitas en las farmacias más cercanas a ti, directamente sobre el mapa.",
      icono:
        "M12 21s-7-6.2-7-11a7 7 0 0 1 14 0c0 4.8-7 11-7 11z M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
    },
    {
      titulo: "Datos confiables",
      descripcion:
        "Los reportes de la comunidad se califican para que sepas qué tan actualizada y segura es cada disponibilidad.",
      icono: "M12 3l7 3v6c0 4.4-3 7.7-7 9-4-1.3-7-4.6-7-9V6l7-3z M9 12l2 2 4-4",
    },
    {
      titulo: "Historial de precios",
      descripcion:
        "Compara precios entre farmacias y revisa cómo han cambiado con el tiempo antes de comprar.",
      icono: "M4 19h16 M7 15l4-4 3 3 5-6",
    },
    {
      titulo: "Farmacias aliadas",
      descripcion:
        "Las farmacias verificadas actualizan su inventario desde su propio panel para mantener la información al día.",
      icono: "M4 9l1.5-5h13L20 9 M4 9h16v11H4z M10 20v-5h4v5",
    },
  ];

  // TODO: reemplazar por los datos de contacto oficiales.
  /** Datos de contacto (provisionales). */
  protected readonly mediosContacto: readonly MedioContacto[] = [
    {
      titulo: "Correo",
      valor: "contacto@medlab.example",
      enlace: "mailto:contacto@medlab.example",
      icono: "M4 6h16v12H4z M4 7l8 6 8-6",
    },
    {
      titulo: "Ubicación",
      valor: "Medellín, Antioquia, Colombia",
      icono:
        "M12 21s-7-6.2-7-11a7 7 0 0 1 14 0c0 4.8-7 11-7 11z M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
    },
    {
      titulo: "Horario de atención",
      valor: "Lunes a viernes, 8:00 a. m. a 6:00 p. m.",
      icono: "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z M12 7v5l3 2",
    },
  ];

  /** Año para el pie de página. */
  protected readonly anioActual: number = new Date().getFullYear();

  constructor() {
    inject(MetadatosService).definirDescripcion(LandingComponent.DESCRIPCION);
  }
}