import {
  ChangeDetectionStrategy,
  Component,
  InputSignal,
  OutputEmitterRef,
  Signal,
  WritableSignal,
  computed,
  inject,
  input,
  output,
  signal,
} from "@angular/core";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { switchMap } from "rxjs";

import {
  AuditoriaEstado,
  DisponibilidadAdmin,
  FarmaciaHistorial,
  PanelAdminService,
} from "../../../core/services/panel-admin.service";
import { formatearFechaColombia } from "../../../core/services/panel-farmacia.service";
import { CalificacionesDisponibilidadComponent } from "../calificaciones-disponibilidad/calificaciones-disponibilidad.component";
import { EstadoLista, aEstadoLista } from "../estado-lista";

/**
 * 
 * cuántas filas se muestran por página en cada tabla del detalle
 * 
 */
const TAMANO_PAGINA: number = 10;

/**
 * 
 * el detalle de una farmacia para el administrador: el historial de cambios de estado
 * (auditoría) y sus publicaciones, con las calificaciones de cada una para eliminar las falsas
 * 
 * el estado "suspendida" que pone el sistema aparece en la auditoría como automático
 * 
 */
@Component({
  selector: "app-farmacia-detalle-admin",
  imports: [CalificacionesDisponibilidadComponent],
  templateUrl: "./farmacia-detalle-admin.component.html",
  styleUrls: ["../panel-admin-comun.css", "./farmacia-detalle-admin.component.css"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FarmaciaDetalleAdminComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);

  readonly farmacia: InputSignal<FarmaciaHistorial> = input.required<FarmaciaHistorial>();
  readonly volver: OutputEmitterRef<void> = output<void>();

  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;
  private readonly etiquetaEstadoPublicacion: Record<string, string> = {
    disponible: "Disponible",
    agotado: "Agotado",
    bajo_pedido: "Bajo pedido",
  };

  protected readonly seccion: WritableSignal<"auditoria" | "publicaciones"> = signal("auditoria");

  protected readonly paginaAuditoria: WritableSignal<number> = signal(0);
  protected readonly paginaPublicaciones: WritableSignal<number> = signal(0);
  private readonly recargaPublicaciones: WritableSignal<number> = signal(0);

  protected readonly auditoria: Signal<EstadoLista<AuditoriaEstado>> = toSignal(
    toObservable(this.paginaAuditoria).pipe(
      switchMap((pagina) => aEstadoLista(this.panel.listarAuditoria(this.farmacia().id, pagina, TAMANO_PAGINA))),
    ),
    { initialValue: { tipo: "cargando" } as EstadoLista<AuditoriaEstado> },
  );

  private readonly consultaPublicaciones: Signal<{ pagina: number; recarga: number }> = computed(() => ({
    pagina: this.paginaPublicaciones(),
    recarga: this.recargaPublicaciones(),
  }));

  protected readonly publicaciones: Signal<EstadoLista<DisponibilidadAdmin>> = toSignal(
    toObservable(this.consultaPublicaciones).pipe(
      switchMap((consulta) =>
        aEstadoLista(this.panel.listarDisponibilidades(this.farmacia().id, consulta.pagina, TAMANO_PAGINA)),
      ),
    ),
    { initialValue: { tipo: "cargando" } as EstadoLista<DisponibilidadAdmin> },
  );

  /**
   * 
   * la publicación cuyas calificaciones se están viendo. Es null si no hay ninguna abierta
   * 
   */
  protected readonly publicacionElegida: WritableSignal<DisponibilidadAdmin | null> = signal(null);

  protected etiquetaEstado(estado: string): string {
    return this.etiquetaEstadoPublicacion[estado] ?? estado;
  }

  protected cambiarSeccion(seccion: "auditoria" | "publicaciones"): void {
    this.seccion.set(seccion);
  }

  protected irAPaginaAuditoria(numero: number): void {
    this.paginaAuditoria.set(numero);
  }

  protected irAPaginaPublicaciones(numero: number): void {
    this.publicacionElegida.set(null);
    this.paginaPublicaciones.set(numero);
  }

  protected verCalificaciones(publicacion: DisponibilidadAdmin): void {
    this.publicacionElegida.set(publicacion);
  }

  protected cerrarCalificaciones(): void {
    this.publicacionElegida.set(null);
  }

  /**
   * 
   * al eliminar una calificación cambia el total de la publicación, así que se vuelve a
   * pedir la tabla para que el número se actualice
   * 
   */
  protected alEliminarCalificacion(): void {
    this.recargaPublicaciones.update((n) => n + 1);
  }
}