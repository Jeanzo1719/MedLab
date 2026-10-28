import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
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
import { takeUntilDestroyed, toObservable, toSignal } from "@angular/core/rxjs-interop";
import { switchMap } from "rxjs";

import { CalificacionAdmin, PanelAdminService } from "../../../core/services/panel-admin.service";
import { formatearFechaColombia, mensajeDeError } from "../../../core/services/panel-farmacia.service";
import { EstadoLista, aEstadoLista } from "../estado-lista";

/**
 * 
 * cuántas calificaciones se muestran por página
 * 
 */
const TAMANO_PAGINA: number = 10;

/**
 * 
 * un mensaje de resultado que se muestra arriba de la tabla, de éxito o de error
 * 
 */
interface Aviso {
  tipo: "ok" | "error";
  texto: string;
}

/**
 * 
 * las calificaciones de una publicación, con quién las hizo, para que el administrador
 * elimine las falsas. Eliminar una calificación no toca la publicación
 * 
 * lo usan el detalle de una farmacia y la lista de publicaciones sospechosas. Avisa con
 * calificacionEliminada para que quien lo use refresque sus totales
 * 
 */
@Component({
  selector: "app-calificaciones-disponibilidad",
  templateUrl: "./calificaciones-disponibilidad.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalificacionesDisponibilidadComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  readonly disponibilidadId: InputSignal<number> = input.required<number>();
  readonly calificacionEliminada: OutputEmitterRef<void> = output<void>();

  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;

  /**
   * 
   * la página elegida guarda de qué publicación era: si cambia la publicación, la página
   * vuelve a la primera sola, sin tener que reaccionar al cambio
   * 
   */
  private readonly paginaElegida: WritableSignal<{ id: number; numero: number }> = signal({ id: -1, numero: 0 });
  protected readonly pagina: Signal<number> = computed(() => {
    const elegida = this.paginaElegida();
    return elegida.id === this.disponibilidadId() ? elegida.numero : 0;
  });
  private readonly recarga: WritableSignal<number> = signal(0);

  private readonly consulta: Signal<{ id: number; pagina: number; recarga: number }> = computed(() => ({
    id: this.disponibilidadId(),
    pagina: this.pagina(),
    recarga: this.recarga(),
  }));

  protected readonly estado: Signal<EstadoLista<CalificacionAdmin>> = toSignal(
    toObservable(this.consulta).pipe(
      switchMap((consulta) =>
        aEstadoLista(this.panel.listarCalificaciones(consulta.id, consulta.pagina, TAMANO_PAGINA)),
      ),
    ),
    { initialValue: { tipo: "cargando" } as EstadoLista<CalificacionAdmin> },
  );

  protected readonly aviso: WritableSignal<Aviso | null> = signal(null);

  /**
   * 
   * la calificación que se quiere eliminar y está esperando confirmación. Es null si
   * no hay ninguna
   * 
   */
  protected readonly porEliminar: WritableSignal<CalificacionAdmin | null> = signal(null);
  protected readonly procesando: WritableSignal<boolean> = signal(false);

  protected irAPagina(numero: number): void {
    this.paginaElegida.set({ id: this.disponibilidadId(), numero });
  }

  protected cerrarAviso(): void {
    this.aviso.set(null);
  }

  protected pedirEliminar(calificacion: CalificacionAdmin): void {
    this.aviso.set(null);
    this.porEliminar.set(calificacion);
  }

  protected cancelarEliminar(): void {
    this.porEliminar.set(null);
  }

  protected confirmarEliminar(): void {
    const calificacion: CalificacionAdmin | null = this.porEliminar();
    if (!calificacion) {
      return;
    }

    // Si era la única de la última página, hay que retroceder: esa página dejará de existir.
    const actual: EstadoLista<CalificacionAdmin> = this.estado();
    if (actual.tipo === "listo" && actual.resultado.contenido.length === 1 && this.pagina() > 0) {
      this.irAPagina(this.pagina() - 1);
    }

    this.procesando.set(true);
    this.panel
      .eliminarCalificacion(calificacion.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.procesando.set(false);
          this.porEliminar.set(null);
          this.aviso.set({ tipo: "ok", texto: "Se eliminó la calificación" });
          this.recarga.update((n) => n + 1);
          this.calificacionEliminada.emit();
        },
        error: (error: unknown) => {
          this.procesando.set(false);
          this.porEliminar.set(null);
          this.aviso.set({ tipo: "error", texto: mensajeDeError(error) });
        },
      });
  }
}