import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  Signal,
  WritableSignal,
  computed,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed, toObservable, toSignal } from "@angular/core/rxjs-interop";
import { switchMap } from "rxjs";

import {
  FarmaciaConAlertas,
  PanelAdminService,
  RevisionInactividad,
} from "../../../core/services/panel-admin.service";
import { formatearFechaColombia, mensajeDeError } from "../../../core/services/panel-farmacia.service";
import { EstadoLista, aEstadoLista } from "../estado-lista";

/**
 * 
 * cuántas farmacias se muestran por página
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
 * las farmacias con alertas de inactividad y el botón para lanzar la revisión a mano
 * 
 * la revisión es la misma que hace el job diario de las 3:00 a. m. y puede suspender
 * farmacias, por eso pide confirmación antes de lanzarla
 * 
 */
@Component({
  selector: "app-alertas-admin",
  templateUrl: "./alertas-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlertasAdminComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;

  protected readonly pagina: WritableSignal<number> = signal(0);
  private readonly recarga: WritableSignal<number> = signal(0);

  private readonly consulta: Signal<{ pagina: number; recarga: number }> = computed(() => ({
    pagina: this.pagina(),
    recarga: this.recarga(),
  }));

  protected readonly estado: Signal<EstadoLista<FarmaciaConAlertas>> = toSignal(
    toObservable(this.consulta).pipe(
      switchMap((consulta) =>
        aEstadoLista(this.panel.listarFarmaciasConAlertas(consulta.pagina, TAMANO_PAGINA)),
      ),
    ),
    { initialValue: { tipo: "cargando" } as EstadoLista<FarmaciaConAlertas> },
  );

  protected readonly aviso: WritableSignal<Aviso | null> = signal(null);
  protected readonly confirmando: WritableSignal<boolean> = signal(false);
  protected readonly revisando: WritableSignal<boolean> = signal(false);

  protected irAPagina(numero: number): void {
    this.pagina.set(numero);
  }

  protected cerrarAviso(): void {
    this.aviso.set(null);
  }

  protected pedirRevision(): void {
    this.aviso.set(null);
    this.confirmando.set(true);
  }

  protected cancelarRevision(): void {
    this.confirmando.set(false);
  }

  protected confirmarRevision(): void {
    this.revisando.set(true);
    this.panel
      .revisarInactividad()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resultado: RevisionInactividad) => {
          this.revisando.set(false);
          this.confirmando.set(false);
          this.aviso.set({ tipo: "ok", texto: this.describir(resultado) });
          this.recarga.update((n) => n + 1);
        },
        error: (error: unknown) => {
          this.revisando.set(false);
          this.confirmando.set(false);
          this.aviso.set({ tipo: "error", texto: mensajeDeError(error) });
        },
      });
  }

  private describir(resultado: RevisionInactividad): string {
    const alertas: string =
      resultado.alertasGeneradas === 1 ? "1 alerta generada" : `${resultado.alertasGeneradas} alertas generadas`;
    const suspendidas: string =
      resultado.farmaciasSuspendidas === 1
        ? "1 farmacia suspendida"
        : `${resultado.farmaciasSuspendidas} farmacias suspendidas`;
    return `Revisión terminada: ${alertas} y ${suspendidas}.`;
  }
}