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

type TipoDecision = "aprobar" | "rechazar";

interface Decision {
  farmacia: FarmaciaPendiente;
  tipo: TipoDecision;
}

type EstadoSolicitudes =
  | { tipo: "cargando" }
  | { tipo: "listo"; farmacias: FarmaciaPendiente[] }
  | { tipo: "error" };

type AvisoDecision = { error: boolean; texto: string };

@Component({
  selector: "app-solicitudes-admin",
  templateUrl: "./solicitudes-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SolicitudesAdminComponent {
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  private readonly recarga: WritableSignal<number> = signal(0);

  protected readonly estado: Signal<EstadoSolicitudes> = toSignal(
    toObservable(this.recarga).pipe(switchMap(() => this.seguirSolicitudes())),
    { initialValue: { tipo: "cargando" } as EstadoSolicitudes },
  );

  protected readonly porDecidir: WritableSignal<Decision | null> = signal<Decision | null>(null);
  protected readonly procesando: WritableSignal<boolean> = signal(false);
  protected readonly aviso: WritableSignal<AvisoDecision | null> = signal<AvisoDecision | null>(null);

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

  private seguirSolicitudes(): Observable<EstadoSolicitudes> {
    return this.farmaciaService.seguirPendientes().pipe(
      map((farmacias: FarmaciaPendiente[]): EstadoSolicitudes => ({ tipo: "listo", farmacias })),
      catchError(() => of<EstadoSolicitudes>({ tipo: "error" })),
      startWith<EstadoSolicitudes>({ tipo: "cargando" }),
    );
  }
}
