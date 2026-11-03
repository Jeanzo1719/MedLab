import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";

import { AlertaInactividad } from "../../core/services/panel-farmacia.service";
import { SesionService } from "../../core/services/sesion.service";
import { StompService } from "../../core/services/stomp.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";

/**
 *
 * el panel de la farmacia: la barra de arriba con las pestañas, el aviso de las
 * alertas de inactividad y, debajo, la sección que esté abierta
 *
 * no carga datos; cada sección lo hace por su cuenta. Es una ruta con hijas
 * (/farmacia/resumen y /farmacia/inventario), definidas en app.routes.ts. El
 * guard de la ruta exige una sesión con rol "farmacia"
 *
 */
@Component({
  selector: "app-panel-farmacia",
  imports: [RouterLink, RouterLinkActive, RouterOutlet, BotonTemaComponent],
  templateUrl: "./panel-farmacia.component.html",
  styleUrl: "./panel-farmacia.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelFarmaciaComponent {
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly router: Router = inject(Router);
  private readonly stomp: StompService = inject(StompService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  /**
   *
   * la última alerta de inactividad que llegó, para mostrarla hasta que se cierre.
   * Es null si no hay ninguna
   *
   */
  protected readonly alerta: WritableSignal<AlertaInactividad | null> = signal<AlertaInactividad | null>(null);

  /**
   *
   * escucha las alertas de esta farmacia mientras el panel esté abierto. El backend
   * solo deja suscribirse a las de la propia cuenta
   *
   */
  constructor() {
    const farmaciaId: number | null = this.sesionService.usuarioId();
    if (farmaciaId === null) {
      return;
    }

    this.stomp
      .escuchar<AlertaInactividad>(`/topic/farmacias/${farmaciaId}/alertas`)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((alerta: AlertaInactividad): void => this.alerta.set(alerta));
  }

  protected cerrarAlerta(): void {
    this.alerta.set(null);
  }

  /**
   *
   * cierra la sesión y vuelve a la landing. El token deja de servir también en
   * el backend
   *
   */
  protected cerrarSesion(): void {
    this.sesionService.cerrarSesion();
    void this.router.navigateByUrl("/");
  }
}
