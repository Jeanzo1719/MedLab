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
import { Coordenadas, UbicacionService } from "../../core/services/ubicacion.service";
import { EncabezadoComponent } from "../../shared/components/encabezado/encabezado.component";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";

const LONGITUD_MAXIMA_DIRECCION: number = 200;

const CODIGO_PERMISO_DENEGADO: number = 1;

type EstadoUbicacion =
  | { tipo: "pendiente" }
  | { tipo: "obteniendo" }
  | { tipo: "lista"; origen: string }
  | { tipo: "denegada" }
  | { tipo: "no-encontrada"; direccion: string }
  | { tipo: "error" };

type EstadoFarmacias =
  | { tipo: "inactivo" }
  | { tipo: "cargando" }
  | { tipo: "resultados"; farmacias: FarmaciaCercana[] }
  | { tipo: "error" };

@Component({
  selector: "app-farmacias-cercanas",
  imports: [EncabezadoComponent, MapaComponent, RouterLink],
  templateUrl: "./farmacias-cercanas.component.html",
  styleUrl: "./farmacias-cercanas.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FarmaciasCercanasComponent {
  private static readonly DESCRIPCION: string =
    "Encuentra las farmacias más cercanas a ti que tienen disponible el medicamento que buscas, ordenadas por distancia.";

  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);

  private readonly ubicacionService: UbicacionService = inject(UbicacionService);

  private readonly formatoKilometros: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    maximumFractionDigits: 1,
  });

  private readonly parametros: Signal<ParamMap> = toSignal(inject(ActivatedRoute).queryParamMap, {
    requireSync: true,
  });

  protected readonly longitudMaximaDireccion: number = LONGITUD_MAXIMA_DIRECCION;

  protected readonly medicamentoId: Signal<number | null> = computed((): number | null => {
    const id: number = Number(this.parametros().get("medicamento"));
    return Number.isInteger(id) && id > 0 ? id : null;
  });

  protected readonly nombreMedicamento: Signal<string> = computed(
    (): string => this.parametros().get("nombre")?.trim() || "el medicamento",
  );

  protected readonly direccion: WritableSignal<string> = signal("");

  protected readonly ubicacion: WritableSignal<Coordenadas | null> = signal<Coordenadas | null>(null);

  protected readonly estadoUbicacion: WritableSignal<EstadoUbicacion> = signal<EstadoUbicacion>({
    tipo: "pendiente",
  });

  protected readonly estadoFarmacias: Signal<EstadoFarmacias> = toSignal(
    combineLatest([toObservable(this.medicamentoId), toObservable(this.ubicacion)]).pipe(
      switchMap(([medicamentoId, ubicacion]) => this.buscarFarmacias(medicamentoId, ubicacion)),
    ),
    { initialValue: { tipo: "inactivo" } as EstadoFarmacias },
  );

  protected readonly farmacias: Signal<FarmaciaCercana[]> = computed((): FarmaciaCercana[] => {
    const estado: EstadoFarmacias = this.estadoFarmacias();
    return estado.tipo === "resultados" ? estado.farmacias : [];
  });

  protected readonly seleccionada: WritableSignal<number | null> = linkedSignal<
    FarmaciaCercana[],
    number | null
  >({
    source: this.farmacias,
    computation: (): number | null => null,
  });

  constructor() {
    inject(MetadatosService).definirDescripcion(FarmaciasCercanasComponent.DESCRIPCION);
  }

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

  protected seleccionarDesdeMapa(farmaciaId: number): void {
    this.seleccionada.set(farmaciaId);
    document
      .getElementById(`farmacia-${farmaciaId}`)
      ?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  protected formatearDistancia(distanciaKm: number): string {
    return distanciaKm < 1
      ? `${Math.round(distanciaKm * 1000)} m`
      : `${this.formatoKilometros.format(distanciaKm)} km`;
  }

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
