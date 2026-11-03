import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  effect,
  inject,
  input,
  output,
  signal,
  untracked,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Subscription } from "rxjs";

import {
  MedicamentoDetalle,
  MedicamentoRecomendado,
  MedicamentoService,
  ReporteDisponibilidad,
} from "../../../../core/services/medicamento.service";

/**
 *
 * la ficha lateral del medicamento elegido en el buscador (HU-3)
 *
 * muestra sus datos, las farmacias que lo reportaron con precio y estado, el
 * resultado de las calificaciones recientes y los demás medicamentos con el
 * mismo principio activo. También permite calificar cada reporte
 *
 */
@Component({
  selector: "app-detalle-medicamento",
  templateUrl: "./detalle-medicamento.component.html",
  styleUrl: "./detalle-medicamento.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetalleMedicamentoComponent {
  private readonly medicamentoService: MedicamentoService = inject(MedicamentoService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);
  private detalleActual: Subscription | null = null;
  private recomendacionesActual: Subscription | null = null;
  private temporizador: ReturnType<typeof setTimeout> | null = null;

  /** el medicamento cuya ficha se muestra */
  readonly medicamentoId = input.required<number>();

  /** pide cerrar la ficha */
  readonly cerrar = output<void>();

  /** pide abrir la ficha de otro medicamento recomendado */
  readonly seleccionar = output<number>();

  /** avisa los nombres de las farmacias que reportaron el medicamento, para el mapa */
  readonly farmaciasReportadas = output<string[]>();

  protected readonly detalle = signal<MedicamentoDetalle | null>(null);
  protected readonly recomendaciones = signal<MedicamentoRecomendado[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal("");
  protected readonly calificando = signal<number | null>(null);
  protected readonly calificacionEnviada = signal<number | null>(null);
  protected readonly errorCalificacion = signal("");

  private readonly formatoPrecio: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  });

  constructor() {
    effect((): void => {
      const id: number = this.medicamentoId();
      untracked((): void => this.cargar(id));
    });

    this.destroyRef.onDestroy((): void => {
      this.detalleActual?.unsubscribe();
      this.recomendacionesActual?.unsubscribe();
      if (this.temporizador !== null) {
        clearTimeout(this.temporizador);
      }
    });
  }

  protected formatearPrecio(valor: number | null): string {
    return valor === null ? "Sin precio" : this.formatoPrecio.format(valor);
  }

  protected etiquetaEstado(estado: string | null): string {
    switch (estado) {
      case "disponible":
        return "Disponible";
      case "bajo_pedido":
        return "Bajo pedido";
      case "agotado":
        return "Agotado";
      default:
        return "Sin estado";
    }
  }

  protected claseEstado(estado: string | null): string {
    return estado === "disponible" || estado === "bajo_pedido" || estado === "agotado"
      ? `estado-${estado}`
      : "estado-sin-dato";
  }

  protected etiquetaTendencia(tendencia: string): string {
    switch (tendencia) {
      case "DISPONIBLE":
        return "La comunidad confirma que hay";
      case "BAJO_PEDIDO":
        return "La comunidad dice que es bajo pedido";
      case "NO_DISPONIBLE":
        return "La comunidad dice que no hay";
      case "SIN_CONSENSO":
        return "Opiniones divididas";
      default:
        return "Sin calificaciones todavía";
    }
  }

  protected claseTendencia(tendencia: string): string {
    switch (tendencia) {
      case "DISPONIBLE":
        return "tendencia-disponible";
      case "BAJO_PEDIDO":
        return "tendencia-bajo-pedido";
      case "NO_DISPONIBLE":
        return "tendencia-no-disponible";
      case "SIN_CONSENSO":
        return "tendencia-sin-consenso";
      default:
        return "tendencia-sin-calificaciones";
    }
  }

  protected calificar(reporte: ReporteDisponibilidad, tipoCalificacionId: number): void {
    this.calificando.set(reporte.disponibilidadId);
    this.errorCalificacion.set("");

    this.medicamentoService
      .calificar(reporte.disponibilidadId, tipoCalificacionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (): void => {
          this.calificando.set(null);
          this.calificacionEnviada.set(reporte.disponibilidadId);
        },
        error: (error: unknown): void => {
          this.calificando.set(null);
          this.errorCalificacion.set(this.medicamentoService.mensajeDeError(error));
        },
      });
  }

  private cargar(id: number): void {
    this.detalleActual?.unsubscribe();
    this.recomendacionesActual?.unsubscribe();
    if (this.temporizador !== null) {
      clearTimeout(this.temporizador);
    }

    this.detalle.set(null);
    this.cargando.set(true);
    this.error.set("");
    this.calificacionEnviada.set(null);
    this.errorCalificacion.set("");
    this.recomendaciones.set([]);

    this.detalleActual = this.medicamentoService
      .seguirDetalle(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (detalle: MedicamentoDetalle): void => {
          this.detalle.set(detalle);
          this.cargando.set(false);
          this.error.set("");
          this.farmaciasReportadas.emit(
            detalle.reportes.map((reporte: ReporteDisponibilidad): string => reporte.farmacia),
          );
          if (this.temporizador !== null) {
            clearTimeout(this.temporizador);
            this.temporizador = null;
          }
        },
      });

    this.temporizador = setTimeout((): void => {
      if (this.cargando()) {
        this.cargando.set(false);
        this.error.set("No se pudo cargar el detalle. Revisa tu conexión e intenta de nuevo.");
      }
    }, 11000);

    this.recomendacionesActual = this.medicamentoService
      .obtenerRecomendaciones(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (recomendaciones: MedicamentoRecomendado[]): void => {
          this.recomendaciones.set(recomendaciones);
        },
        error: (): void => this.recomendaciones.set([]),
      });
  }
}
