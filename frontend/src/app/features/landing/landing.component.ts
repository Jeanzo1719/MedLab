import { ChangeDetectionStrategy, Component, inject, Signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { RouterLink } from "@angular/router";
import { catchError, of } from "rxjs";

import { FarmaciaMapa, FarmaciaService } from "../../core/services/farmacia.service";
import { MetadatosService } from "../../core/services/metadatos.service";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";
import { BuscadorMedicamentosComponent } from "./components/buscador-medicamentos/buscador-medicamentos.component";
import { EncabezadoComponent } from "../../shared/components/encabezado/encabezado.component";

/**
 *
 * la forma de cada servicio que se muestra en una tarjeta
 *
 */
interface Servicio {
  /**
   *
   * el nombre del servicio, por ejemplo "Historial de precios"
   *
   */
  titulo: string;

  /**
   *
   * el texto que explica el servicio
   *
   */
  descripcion: string;

  /**
   *
   * el dibujo del ícono, escrito como el trazo de un SVG (las letras y números
   * le dicen al navegador por dónde pasar la línea)
   *
   */
  icono: string;
}

/**
 *
 * la forma de cada dato de contacto que se muestra
 *
 */
interface MedioContacto {
  /**
   *
   * qué tipo de dato es, por ejemplo "Correo"
   *
   */
  titulo: string;

  /**
   *
   * el dato en sí, por ejemplo el correo o el horario
   *
   */
  valor: string;

  /**
   *
   * el enlace al que lleva al tocarlo, por ejemplo mailto: para abrir el correo.
   * Es opcional (?): los datos sin enlace se muestran como texto normal
   *
   */
  enlace?: string;

  /**
   *
   * el dibujo del ícono, escrito como el trazo de un SVG
   *
   */
  icono: string;
}

/**
 *
 * la página de la landing, la primera que ve cualquier persona al entrar a
 * MedLab, sin necesidad de cuenta
 *
 * tiene el encabezado, las secciones de inicio, servicios y contacto, y el pie
 * de página. Además le pide al backend las farmacias aprobadas y se las pasa al
 * mapa de la sección de inicio
 *
 */
@Component({
  selector: "app-landing",
  imports: [EncabezadoComponent, BuscadorMedicamentosComponent, MapaComponent, RouterLink],
  templateUrl: "./landing.component.html",
  styleUrl: "./landing.component.css",
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
   * las farmacias aprobadas que llegan del backend, para el mapa
   *
   * empieza como una lista vacía y se llena cuando llega la respuesta. Si hay un
   * error, se queda vacía y el mapa se muestra sin puntos
   *
   */
  protected readonly farmacias: Signal<FarmaciaMapa[]> = toSignal(
    this.farmaciaService
      .listarAprobadas()
      .pipe(catchError(() => of<FarmaciaMapa[]>([]))),
    { initialValue: [] },
  );

  /**
   *
   * los cuatro servicios principales de MedLab, en el orden en que se muestran
   *
   */
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

  // TODO: reemplazar estos valores provisionales por los datos de contacto oficiales de la plataforma
  /**
   *
   * los datos de contacto de MedLab: correo, ubicación y horario
   *
   * son provisionales hasta tener los oficiales (ver el TODO de arriba)
   *
   */
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

  /**
   *
   * el año actual, para el texto de derechos reservados del pie de página. Se
   * calcula solo, así que no hay que cambiarlo cada año
   *
   */
  protected readonly anioActual: number = new Date().getFullYear();

  /**
   *
   * pone la descripción de la landing al abrir la página
   *
   */
  constructor() {
    inject(MetadatosService).definirDescripcion(LandingComponent.DESCRIPCION);
  }
}
