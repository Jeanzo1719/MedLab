import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  Signal,
  WritableSignal,
  computed,
  effect,
  inject,
  input,
  linkedSignal,
  output,
  signal,
  untracked,
} from "@angular/core";
import { takeUntilDestroyed, toObservable, toSignal } from "@angular/core/rxjs-interop";
import { Observable, catchError, combineLatest, map, of, startWith, switchMap } from "rxjs";

import { FarmaciaCercana, FarmaciaService } from "../../../../core/services/farmacia.service";
import { ReporteDisponibilidad } from "../../../../core/services/medicamento.service";
import { MedioRuta, Ruta, RutaService } from "../../../../core/services/ruta.service";
import { Coordenadas, UbicacionService } from "../../../../core/services/ubicacion.service";

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
 * los estados de la ubicación: pendiente (no se ha pedido), obteniendo, lista
 * (con el texto que la describe), denegada, no-encontrada o error
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
 * los estados de la búsqueda de farmacias cercanas: inactivo (falta la ubicación),
 * cargando, resultados con la lista, o error
 *
 */
type EstadoFarmacias =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; farmacias: FarmaciaCercana[] }
  | { tipo: "error" };

/**
 *
 * los estados de la ruta hasta la farmacia elegida: inactivo, cargando, lista con
 * la ruta, sin-ruta si no hay camino, o error
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
 * la parte "Cerca de ti" de la ficha del medicamento (HU-3). Igual que la página de
 * farmacias cercanas de la landing: el usuario da su ubicación o escribe una
 * dirección, ve las farmacias más cercanas que tienen el medicamento disponible,
 * elige una y ve cómo llegar a pie o en carro
 *
 * avisa la farmacia elegida (para que el mapa la marque) y la ruta (para dibujarla)
 *
 */
@Component({
  selector: "app-cercania-medicamento",
  templateUrl: "./cercania-medicamento.component.html",
  styleUrl: "./cercania-medicamento.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CercaniaMedicamentoComponent {
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);
  private readonly ubicacionService: UbicacionService = inject(UbicacionService);
  private readonly rutaService: RutaService = inject(RutaService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  /** el medicamento cuyas farmacias cercanas se buscan */
  readonly medicamentoId = input.required<number>();

  /** los reportes del medicamento, para mostrar el mensaje de disponibilidad de cada farmacia */
  readonly reportes = input<ReporteDisponibilidad[]>([]);

  /** la farmacia que se eligió desde el mapa, o null */
  readonly farmaciaElegida = input<number | null>(null);

  /** avisa la farmacia que quedó elegida en la lista, o null si no hay ninguna */
  readonly elegir = output<number | null>();

  /** avisa los puntos de la ruta hasta la farmacia elegida, para dibujarla en el mapa */
  readonly rutaCambiada = output<Coordenadas[]>();

  protected readonly longitudMaximaDireccion: number = LONGITUD_MAXIMA_DIRECCION;
  protected readonly direccion: WritableSignal<string> = signal("");

  /** dónde está el usuario, guardado en el service para que sobreviva a cerrar el detalle */
  protected readonly ubicacion: Signal<Coordenadas | null> = this.ubicacionService.ubicacionActual;

  protected readonly estadoUbicacion: WritableSignal<EstadoUbicacion> = signal<EstadoUbicacion>(
    this.ubicacionService.ubicacionActual() ? { tipo: "lista", origen: "tu ubicación" } : { tipo: "pendiente" },
  );

  /**
   *
   * las farmacias cercanas que tienen el medicamento disponible. Cada vez que cambia
   * el medicamento o la ubicación, vuelve a buscar; la búsqueda anterior se cancela
   *
   */
  protected readonly estadoFarmacias: Signal<EstadoFarmacias> = toSignal(
    combineLatest([toObservable(this.medicamentoId), toObservable(this.ubicacion)]).pipe(
      switchMap(([medicamentoId, coordenadas]) => this.buscarFarmacias(medicamentoId, coordenadas)),
    ),
    { initialValue: { tipo: "inactivo" } as EstadoFarmacias },
  );

  protected readonly farmacias: Signal<FarmaciaCercana[]> = computed((): FarmaciaCercana[] => {
    const estado: EstadoFarmacias = this.estadoFarmacias();
    return estado.tipo === "resultados" ? estado.farmacias : [];
  });

  /** la farmacia elegida. Vuelve a null cada vez que llega una lista nueva */
  protected readonly seleccionada: WritableSignal<number | null> = linkedSignal<FarmaciaCercana[], number | null>({
    source: this.farmacias,
    computation: (): number | null => null,
  });

  protected readonly farmaciaSeleccionada: Signal<FarmaciaCercana | undefined> = computed(
    (): FarmaciaCercana | undefined =>
      this.farmacias().find((farmacia: FarmaciaCercana): boolean => farmacia.id === this.seleccionada()),
  );

  /** el mensaje de disponibilidad de cada reporte, por su número */
  protected readonly mensajes: Signal<Map<number, string>> = computed(
    (): Map<number, string> =>
      new Map(
        this.reportes().map((reporte: ReporteDisponibilidad): [number, string] => [
          reporte.disponibilidadId,
          reporte.mensajeDisponibilidad,
        ]),
      ),
  );

  protected readonly medios: readonly { valor: MedioRuta; etiqueta: string }[] = [
    { valor: "a-pie", etiqueta: "A pie" },
    { valor: "en-carro", etiqueta: "En carro" },
  ];

  protected readonly medio: WritableSignal<MedioRuta> = signal<MedioRuta>("a-pie");

  /** la ruta hasta la farmacia elegida; se recalcula al cambiar la ubicación, la farmacia o el medio */
  protected readonly estadoRuta: Signal<EstadoRuta> = toSignal(
    combineLatest([
      toObservable(this.ubicacion),
      toObservable(this.farmaciaSeleccionada),
      toObservable(this.medio),
    ]).pipe(switchMap(([coordenadas, farmacia, medio]) => this.trazarRuta(coordenadas, farmacia, medio))),
    { initialValue: { tipo: "inactivo" } as EstadoRuta },
  );

  protected readonly puntosRuta: Signal<Coordenadas[]> = computed((): Coordenadas[] => {
    const estado: EstadoRuta = this.estadoRuta();
    return estado.tipo === "lista" ? estado.ruta.puntos : [];
  });

  private readonly formatoKilometros: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    maximumFractionDigits: 1,
  });

  constructor() {
    // primero se sincroniza la elección que llega desde el mapa, para que no se confunda
    // con un cambio de la lista
    effect((): void => {
      const elegida: number | null = this.farmaciaElegida();
      untracked((): void => this.seleccionada.set(elegida));
    });

    effect((): void => {
      const seleccion: number | null = this.seleccionada();
      untracked((): void => {
        if (seleccion !== this.farmaciaElegida()) {
          this.elegir.emit(seleccion);
        }
      });
    });

    effect((): void => this.rutaCambiada.emit(this.puntosRuta()));
  }

  protected usarMiUbicacion(): void {
    this.estadoUbicacion.set({ tipo: "obteniendo" });
    this.ubicacionService
      .obtenerDelDispositivo()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (coordenadas: Coordenadas): void => {
          this.ubicacionService.ubicacionActual.set(coordenadas);
          this.estadoUbicacion.set({ tipo: "lista", origen: "tu ubicación actual" });
        },
        error: (error: unknown): void => {
          const denegada: boolean =
            error instanceof GeolocationPositionError && error.code === CODIGO_PERMISO_DENEGADO;
          this.estadoUbicacion.set({ tipo: denegada ? "denegada" : "error" });
        },
      });
  }

  protected buscarDireccion(): void {
    const direccion: string = this.direccion().trim();
    if (!direccion) {
      return;
    }

    this.estadoUbicacion.set({ tipo: "obteniendo" });
    this.ubicacionService
      .buscarDireccion(direccion)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (coordenadas: Coordenadas | null): void => {
          if (!coordenadas) {
            this.estadoUbicacion.set({ tipo: "no-encontrada", direccion });
            return;
          }
          this.ubicacionService.ubicacionActual.set(coordenadas);
          this.estadoUbicacion.set({ tipo: "lista", origen: direccion });
        },
        error: (): void => this.estadoUbicacion.set({ tipo: "error" }),
      });
  }

  protected formatearDistancia(distanciaKm: number): string {
    return distanciaKm < 1
      ? `${Math.round(distanciaKm * 1000)} m`
      : `${this.formatoKilometros.format(distanciaKm)} km`;
  }

  protected formatearDuracion(duracionMin: number): string {
    const minutos: number = Math.max(1, Math.round(duracionMin));
    return minutos < 60 ? `${minutos} min` : `${Math.floor(minutos / 60)} h ${minutos % 60} min`;
  }

  private buscarFarmacias(
    medicamentoId: number,
    coordenadas: Coordenadas | null,
  ): Observable<EstadoFarmacias> {
    if (coordenadas === null) {
      return of({ tipo: "inactivo" });
    }

    return this.farmaciaService
      .listarCercanas(medicamentoId, coordenadas.latitud, coordenadas.longitud)
      .pipe(
        map((farmacias: FarmaciaCercana[]): EstadoFarmacias => ({ tipo: "resultados", farmacias })),
        catchError(() => of<EstadoFarmacias>({ tipo: "error" })),
        startWith<EstadoFarmacias>({ tipo: "cargando" }),
      );
  }

  private trazarRuta(
    coordenadas: Coordenadas | null,
    farmacia: FarmaciaCercana | undefined,
    medio: MedioRuta,
  ): Observable<EstadoRuta> {
    if (!coordenadas || !farmacia) {
      return of({ tipo: "inactivo" });
    }

    return this.rutaService.trazar(coordenadas, farmacia, medio).pipe(
      map((ruta: Ruta | null): EstadoRuta => (ruta ? { tipo: "lista", ruta } : { tipo: "sin-ruta" })),
      catchError(() => of<EstadoRuta>({ tipo: "error" })),
      startWith<EstadoRuta>({ tipo: "cargando" }),
    );
  }
}
