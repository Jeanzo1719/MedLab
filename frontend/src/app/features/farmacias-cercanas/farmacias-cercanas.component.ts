import {
  ChangeDetectionStrategy,
  Component,
  Signal,
  WritableSignal,
  computed,
  inject,
  linkedSignal,
  signal,
} from "@angular/core";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute, ParamMap, RouterLink } from "@angular/router";
import { Observable, catchError, combineLatest, map, of, startWith, switchMap } from "rxjs";

import { FarmaciaCercana, FarmaciaService } from "../../core/services/farmacia.service";
import { MetadatosService } from "../../core/services/metadatos.service";
import { MedioRuta, Ruta, RutaService } from "../../core/services/ruta.service";
import { Coordenadas, UbicacionService } from "../../core/services/ubicacion.service";
import { EncabezadoComponent } from "../../shared/components/encabezado/encabezado.component";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";

/**
 *
 * la cantidad máxima de letras que se pueden escribir en la dirección (200)
 *
 */
const LONGITUD_MAXIMA_DIRECCION: number = 200;

/**
 *
 * el número con el que el navegador avisa que el usuario no dio permiso para
 * usar su ubicación (1)
 *
 */
const CODIGO_PERMISO_DENEGADO: number = 1;

/**
 *
 * los estados en que puede estar la ubicación del usuario, para saber qué mensaje
 * mostrar
 *
 * pendiente: todavía no se ha pedido
 * obteniendo: se está buscando (en el dispositivo o en Nominatim)
 * lista: ya se tiene, con origen, que es el texto que se muestra (tu ubicación
 * actual o la dirección escrita)
 * denegada: el usuario no dio permiso
 * no-encontrada: Nominatim no encontró la dirección escrita
 * error: algo falló al buscarla
 *
 */
type EstadoUbicacion =
  | { tipo: "pendiente" }
  | { tipo: "obteniendo" }
  | { tipo: "lista"; origen: string }
  | { tipo: "denegada" }
  | { tipo: "no-encontrada"; direccion: string }
  | { tipo: "error" };

/**
 *
 * los estados en que puede estar la ruta hasta la farmacia elegida
 *
 * inactivo: no hay farmacia elegida o no hay ubicación
 * cargando: la ruta se está trazando
 * lista: llegó la ruta, con su distancia, tiempo y puntos
 * sin-ruta: OSRM no encontró un camino
 * error: la consulta falló
 *
 */
type EstadoRuta =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "lista"; ruta: Ruta }
  | { tipo: "sin-ruta" }
  | { tipo: "error" };

/**
 *
 * los estados en que puede estar la búsqueda de farmacias cercanas
 *
 * inactivo: falta el medicamento o la ubicación
 * cargando: la búsqueda se está haciendo
 * resultados: llegó la respuesta, con la lista de farmacias (puede estar vacía)
 * error: la búsqueda falló o el backend no respondió
 *
 */
type EstadoFarmacias =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; farmacias: FarmaciaCercana[] }
  | { tipo: "error" };

/**
 *
 * la página de farmacias cercanas. Muestra las farmacias aprobadas más cercanas
 * al usuario que tienen disponible el medicamento que eligió en el buscador
 *
 * el medicamento llega en la dirección de la página (medicamento y nombre). El
 * usuario da su ubicación o escribe una dirección, y la página muestra las
 * farmacias en una lista y en el mapa, con la distancia. Al elegir una farmacia,
 * traza la ruta hasta ella a pie o en carro
 *
 */
@Component({
  selector: "app-farmacias-cercanas",
  imports: [EncabezadoComponent, MapaComponent, RouterLink],
  templateUrl: "./farmacias-cercanas.component.html",
  styleUrl: "./farmacias-cercanas.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FarmaciasCercanasComponent {
  /**
   *
   * la descripción de la página que muestran los buscadores como Google debajo
   * del título
   *
   */
  private static readonly DESCRIPCION: string =
    "Encuentra las farmacias más cercanas a ti que tienen disponible el medicamento que buscas, ordenadas por distancia.";

  /**
   *
   * el service que pide las farmacias cercanas al backend
   *
   */
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);

  /**
   *
   * el service que consigue la ubicación del dispositivo o de una dirección
   *
   */
  private readonly ubicacionService: UbicacionService = inject(UbicacionService);

  /**
   *
   * el service que traza la ruta hasta la farmacia
   *
   */
  private readonly rutaService: RutaService = inject(RutaService);

  /**
   *
   * el formato de los kilómetros: con coma decimal, como se escribe en Colombia,
   * y máximo un decimal (por ejemplo 2,3)
   *
   */
  private readonly formatoKilometros: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    maximumFractionDigits: 1,
  });

  /**
   *
   * los datos que vienen en la dirección de la página, después del ?
   * (medicamento y nombre). Si cambian, todo lo que depende de ellos se actualiza
   *
   */
  private readonly parametros: Signal<ParamMap> = toSignal(inject(ActivatedRoute).queryParamMap, {
    requireSync: true,
  });

  /**
   *
   * la cantidad máxima de letras de la dirección, para limitar el campo en el HTML
   *
   */
  protected readonly longitudMaximaDireccion: number = LONGITUD_MAXIMA_DIRECCION;

  /**
   *
   * el id del medicamento que se busca, tomado de la dirección de la página
   *
   * si no viene o no es un número entero mayor que 0, es null y la página pide
   * elegir un medicamento
   *
   */
  protected readonly medicamentoId: Signal<number | null> = computed((): number | null => {
    const id: number = Number(this.parametros().get("medicamento"));
    return Number.isInteger(id) && id > 0 ? id : null;
  });

  /**
   *
   * el nombre del medicamento para los textos de la página, tomado de la
   * dirección. Si no viene, dice "el medicamento"
   *
   */
  protected readonly nombreMedicamento: Signal<string> = computed(
    (): string => this.parametros().get("nombre")?.trim() || "el medicamento",
  );

  /**
   *
   * la dirección o ciudad que el usuario va escribiendo. Empieza vacía
   *
   */
  protected readonly direccion: WritableSignal<string> = signal("");

  /**
   *
   * dónde está el usuario. Empieza en null y se llena cuando se obtiene su
   * ubicación o la de la dirección escrita
   *
   */
  protected readonly ubicacion: WritableSignal<Coordenadas | null> = signal<Coordenadas | null>(null);

  /**
   *
   * el estado de la ubicación, que el HTML usa para saber qué mensaje mostrar.
   * Empieza en pendiente
   *
   */
  protected readonly estadoUbicacion: WritableSignal<EstadoUbicacion> = signal<EstadoUbicacion>({
    tipo: "pendiente",
  });

  /**
   *
   * el estado de la búsqueda de farmacias cercanas
   *
   * cada vez que cambia el medicamento o la ubicación, vuelve a buscar. Si llega
   * un cambio antes de que termine la búsqueda anterior, esa se cancela y solo
   * vale la última (eso hace switchMap). Empieza en inactivo
   *
   */
  protected readonly estadoFarmacias: Signal<EstadoFarmacias> = toSignal(
    combineLatest([toObservable(this.medicamentoId), toObservable(this.ubicacion)]).pipe(
      switchMap(([medicamentoId, ubicacion]) => this.buscarFarmacias(medicamentoId, ubicacion)),
    ),
    { initialValue: { tipo: "inactivo" } as EstadoFarmacias },
  );

  /**
   *
   * la lista de farmacias encontradas, para la lista y el mapa. Mientras no haya
   * resultados, es una lista vacía
   *
   */
  protected readonly farmacias: Signal<FarmaciaCercana[]> = computed((): FarmaciaCercana[] => {
    const estado: EstadoFarmacias = this.estadoFarmacias();
    return estado.tipo === "resultados" ? estado.farmacias : [];
  });

  /**
   *
   * el id de la farmacia que eligió el usuario, o null si no ha elegido ninguna
   *
   * se puede cambiar al tocar una farmacia, pero vuelve a null solo cada vez que
   * llega una lista nueva de farmacias (eso hace linkedSignal), así no queda
   * elegida una farmacia que ya no está en la lista
   *
   */
  protected readonly seleccionada: WritableSignal<number | null> = linkedSignal<
    FarmaciaCercana[],
    number | null
  >({
    source: this.farmacias,
    computation: (): number | null => null,
  });

  /**
   *
   * las opciones del selector de medio, con el texto de cada botón
   *
   */
  protected readonly medios: readonly { valor: MedioRuta; etiqueta: string }[] = [
    { valor: "a-pie", etiqueta: "A pie" },
    { valor: "en-carro", etiqueta: "En carro" },
  ];

  /**
   *
   * el medio elegido para la ruta. Empieza en a pie, porque las farmacias
   * suelen estar cerca
   *
   */
  protected readonly medio: WritableSignal<MedioRuta> = signal<MedioRuta>("a-pie");

  /**
   *
   * la farmacia elegida completa (con su nombre y coordenadas), buscada en la
   * lista por su id. Si no hay ninguna elegida, es undefined
   *
   */
  protected readonly farmaciaSeleccionada: Signal<FarmaciaCercana | undefined> = computed(
    (): FarmaciaCercana | undefined =>
      this.farmacias().find((farmacia: FarmaciaCercana): boolean => farmacia.id === this.seleccionada()),
  );

  /**
   *
   * el estado de la ruta hasta la farmacia elegida
   *
   * cada vez que cambia la ubicación, la farmacia elegida o el medio, vuelve a
   * trazarla. Si llega un cambio antes de que termine, la consulta anterior se
   * cancela (switchMap). Empieza en inactivo
   *
   */
  protected readonly estadoRuta: Signal<EstadoRuta> = toSignal(
    combineLatest([
      toObservable(this.ubicacion),
      toObservable(this.farmaciaSeleccionada),
      toObservable(this.medio),
    ]).pipe(switchMap(([ubicacion, farmacia, medio]) => this.trazarRuta(ubicacion, farmacia, medio))),
    { initialValue: { tipo: "inactivo" } as EstadoRuta },
  );

  /**
   *
   * los puntos de la ruta para que el mapa dibuje la línea. Mientras no haya
   * ruta, es una lista vacía
   *
   */
  protected readonly puntosRuta: Signal<Coordenadas[]> = computed((): Coordenadas[] => {
    const estado: EstadoRuta = this.estadoRuta();
    return estado.tipo === "lista" ? estado.ruta.puntos : [];
  });

  /**
   *
   * pone la descripción de la página al abrirla
   *
   */
  constructor() {
    inject(MetadatosService).definirDescripcion(FarmaciasCercanasComponent.DESCRIPCION);
  }

  /**
   *
   * pide la ubicación del dispositivo cuando el usuario toca "Usar mi ubicación"
   *
   * mientras espera, el estado queda en obteniendo. Si llega, la guarda (eso
   * dispara la búsqueda de farmacias). Si el usuario no dio permiso, el estado
   * queda en denegada; si falló por otra razón, en error
   *
   */
  protected usarMiUbicacion(): void {
    this.estadoUbicacion.set({ tipo: "obteniendo" });
    this.ubicacionService.obtenerDelDispositivo().subscribe({
      next: (coordenadas: Coordenadas): void => {
        this.ubicacion.set(coordenadas);
        this.estadoUbicacion.set({ tipo: "lista", origen: "tu ubicación actual" });
      },
      error: (error: unknown): void => {
        const denegada: boolean =
          error instanceof GeolocationPositionError && error.code === CODIGO_PERMISO_DENEGADO;
        this.estadoUbicacion.set({ tipo: denegada ? "denegada" : "error" });
      },
    });
  }

  /**
   *
   * busca las coordenadas de la dirección escrita cuando el usuario presiona
   * "Buscar"
   *
   * si el campo está vacío, no hace nada. Si Nominatim encuentra la dirección,
   * guarda la ubicación (eso dispara la búsqueda de farmacias); si no la
   * encuentra, el estado queda en no-encontrada, y si falla, en error
   *
   */
  protected buscarDireccion(): void {
    const direccion: string = this.direccion().trim();
    if (!direccion) {
      return;
    }

    this.estadoUbicacion.set({ tipo: "obteniendo" });
    this.ubicacionService.buscarDireccion(direccion).subscribe({
      next: (coordenadas: Coordenadas | null): void => {
        if (!coordenadas) {
          this.estadoUbicacion.set({ tipo: "no-encontrada", direccion });
          return;
        }
        this.ubicacion.set(coordenadas);
        this.estadoUbicacion.set({ tipo: "lista", origen: direccion });
      },
      error: (): void => this.estadoUbicacion.set({ tipo: "error" }),
    });
  }

  /**
   *
   * elige la farmacia que el usuario tocó en el mapa
   *
   * la marca como elegida y mueve la lista hasta ella, para que se vea cuál es
   *
   */
  protected seleccionarDesdeMapa(farmaciaId: number): void {
    this.seleccionada.set(farmaciaId);
    document
      .getElementById(`farmacia-${farmaciaId}`)
      ?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  /**
   *
   * convierte la distancia en el texto que se muestra
   *
   * si es menos de 1 km, la muestra en metros sin decimales (por ejemplo 850 m);
   * si no, en kilómetros con un decimal (por ejemplo 2,3 km)
   *
   */
  protected formatearDistancia(distanciaKm: number): string {
    return distanciaKm < 1
      ? `${Math.round(distanciaKm * 1000)} m`
      : `${this.formatoKilometros.format(distanciaKm)} km`;
  }

  /**
   *
   * convierte el tiempo de la ruta en el texto que se muestra
   *
   * lo redondea a minutos (mínimo 1). Si es menos de una hora, lo muestra en
   * minutos (por ejemplo 15 min); si no, en horas y minutos (por ejemplo 1 h 20 min)
   *
   */
  protected formatearDuracion(duracionMin: number): string {
    const minutos: number = Math.max(1, Math.round(duracionMin));
    return minutos < 60 ? `${minutos} min` : `${Math.floor(minutos / 60)} h ${minutos % 60} min`;
  }

  /**
   *
   * traza la ruta y devuelve los estados por los que pasa
   *
   * si falta la ubicación o la farmacia, queda inactivo. Si no, primero devuelve
   * cargando y después lista con la ruta, sin-ruta si OSRM no encontró camino, o
   * error si algo falló
   *
   */
  private trazarRuta(
    ubicacion: Coordenadas | null,
    farmacia: FarmaciaCercana | undefined,
    medio: MedioRuta,
  ): Observable<EstadoRuta> {
    if (!ubicacion || !farmacia) {
      return of({ tipo: "inactivo" });
    }

    return this.rutaService.trazar(ubicacion, farmacia, medio).pipe(
      map((ruta: Ruta | null): EstadoRuta => (ruta ? { tipo: "lista", ruta } : { tipo: "sin-ruta" })),
      catchError(() => of<EstadoRuta>({ tipo: "error" })),
      startWith<EstadoRuta>({ tipo: "cargando" }),
    );
  }

  /**
   *
   * busca las farmacias cercanas y devuelve los estados por los que pasa
   *
   * si falta el medicamento o la ubicación, queda inactivo. Si no, primero
   * devuelve cargando y después resultados con la lista, o error si algo falló
   *
   */
  private buscarFarmacias(
    medicamentoId: number | null,
    ubicacion: Coordenadas | null,
  ): Observable<EstadoFarmacias> {
    if (medicamentoId === null || ubicacion === null) {
      return of({ tipo: "inactivo" });
    }

    return this.farmaciaService
      .listarCercanas(medicamentoId, ubicacion.latitud, ubicacion.longitud)
      .pipe(
        map((farmacias: FarmaciaCercana[]): EstadoFarmacias => ({ tipo: "resultados", farmacias })),
        catchError(() => of<EstadoFarmacias>({ tipo: "error" })),
        startWith<EstadoFarmacias>({ tipo: "cargando" }),
      );
  }
}
