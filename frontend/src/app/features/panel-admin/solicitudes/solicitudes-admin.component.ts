import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  Signal,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed, toObservable, toSignal } from "@angular/core/rxjs-interop";
import { Observable, catchError, map, of, startWith, switchMap } from "rxjs";

import { FarmaciaPendiente, FarmaciaService } from "../../../core/services/farmacia.service";

/**
 *
 * lo que el administrador puede decidir sobre una solicitud
 *
 */
type TipoDecision = "aprobar" | "rechazar";

/**
 *
 * la decisión que el administrador está a punto de confirmar
 *
 */
interface Decision {
  farmacia: FarmaciaPendiente;
  tipo: TipoDecision;
}

/**
 *
 * los estados en que puede estar la lista de solicitudes, para saber qué mostrar
 *
 */
type EstadoSolicitudes =
  | { tipo: "cargando" }
  | { tipo: "listo"; farmacias: FarmaciaPendiente[] }
  | { tipo: "error" };

/**
 *
 * el mensaje que se muestra después de una decisión
 *
 */
type AvisoDecision = { error: boolean; texto: string };

/**
 *
 * las solicitudes de las farmacias nuevas: la tabla con sus datos de validación y
 * los botones para aprobarlas o rechazarlas, siempre con una confirmación antes
 *
 * la lista se actualiza sola cuando cualquier administrador toma una decisión
 *
 */
@Component({
  selector: "app-solicitudes-admin",
  templateUrl: "./solicitudes-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SolicitudesAdminComponent {
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  /**
   *
   * un contador: cada vez que cambia, la lista se vuelve a pedir al backend
   *
   */
  private readonly recarga: WritableSignal<number> = signal(0);

  /**
   *
   * el estado actual de la lista, que el HTML usa para saber qué mostrar
   *
   */
  protected readonly estado: Signal<EstadoSolicitudes> = toSignal(
    toObservable(this.recarga).pipe(switchMap(() => this.seguirSolicitudes())),
    { initialValue: { tipo: "cargando" } as EstadoSolicitudes },
  );

  /**
   *
   * la decisión que falta confirmar. Es null si no hay ninguna
   *
   */
  protected readonly porDecidir: WritableSignal<Decision | null> = signal<Decision | null>(null);

  /**
   *
   * si se está esperando la confirmación del backend. Mientras tanto, los botones
   * quedan desactivados
   *
   */
  protected readonly procesando: WritableSignal<boolean> = signal(false);

  /**
   *
   * el resultado de la última decisión. Es null si no hay nada que avisar
   *
   */
  protected readonly aviso: WritableSignal<AvisoDecision | null> = signal<AvisoDecision | null>(null);

  /**
   *
   * abre la confirmación de una decisión, sin enviar nada todavía
   *
   */
  protected pedirDecision(farmacia: FarmaciaPendiente, tipo: TipoDecision): void {
    this.aviso.set(null);
    this.porDecidir.set({ farmacia, tipo });
  }

  protected cancelarDecision(): void {
    this.porDecidir.set(null);
  }

  protected reintentar(): void {
    this.recarga.update((numero: number): number => numero + 1);
  }

  /**
   *
   * envía la decisión ya confirmada y muestra el resultado
   *
   * si no llega la confirmación, se pide la lista de nuevo, porque otro administrador
   * pudo haber decidido antes sobre esa misma farmacia
   *
   */
  protected confirmarDecision(): void {
    const decision: Decision | null = this.porDecidir();
    if (!decision || this.procesando()) {
      return;
    }

    const aprobar: boolean = decision.tipo === "aprobar";
    const envio: Observable<void> = aprobar
      ? this.farmaciaService.aprobar(decision.farmacia.id)
      : this.farmaciaService.rechazar(decision.farmacia.id);

    this.procesando.set(true);
    envio.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (): void => {
        this.aviso.set({
          error: false,
          texto: `${decision.farmacia.nombre} quedó ${aprobar ? "aprobada" : "rechazada"}.`,
        });
        this.porDecidir.set(null);
        this.procesando.set(false);
      },
      error: (): void => {
        this.aviso.set({
          error: true,
          texto: `No se pudo confirmar la decisión sobre ${decision.farmacia.nombre}. Revisa la lista e intenta de nuevo.`,
        });
        this.porDecidir.set(null);
        this.procesando.set(false);
        this.reintentar();
      },
    });
  }

  /**
   *
   * convierte la lista de pendientes en los estados por los que pasa: cargando, y
   * después listo con las farmacias o error
   *
   */
  private seguirSolicitudes(): Observable<EstadoSolicitudes> {
    return this.farmaciaService.seguirPendientes().pipe(
      map((farmacias: FarmaciaPendiente[]): EstadoSolicitudes => ({ tipo: "listo", farmacias })),
      catchError(() => of<EstadoSolicitudes>({ tipo: "error" })),
      startWith<EstadoSolicitudes>({ tipo: "cargando" }),
    );
  }
}
