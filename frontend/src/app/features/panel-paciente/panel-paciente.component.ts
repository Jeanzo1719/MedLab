import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  Signal,
  computed,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Router, RouterLink } from "@angular/router";

import { FarmaciaMapa, FarmaciaService } from "../../core/services/farmacia.service";
import { Sesion, SesionService } from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";
import { BuscadorPacienteComponent } from "./components/buscador-medicamentos/buscador-medicamentos.component";
import { DetalleMedicamentoComponent } from "./components/detalle-medicamento/detalle-medicamento.component";

/**
 *
 * el panel del paciente/cuidador: el buscador de medicamentos de la HU-3 sobre
 * el mapa de farmacias aprobadas
 *
 * el buscador flota a la izquierda y el detalle del medicamento elegido a la
 * derecha. El mapa ocupa el resto y muestra las farmacias aprobadas
 *
 */
@Component({
  selector: "app-panel-paciente",
  imports: [
    RouterLink,
    BotonTemaComponent,
    MapaComponent,
    BuscadorPacienteComponent,
    DetalleMedicamentoComponent,
  ],
  templateUrl: "./panel-paciente.component.html",
  styleUrl: "./panel-paciente.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelPacienteComponent {
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly farmaciaService: FarmaciaService = inject(FarmaciaService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);
  private readonly router: Router = inject(Router);

  protected readonly sesion: Signal<Sesion | null> = this.sesionService.sesionActual;
  protected readonly farmacias = signal<FarmaciaMapa[]>([]);
  protected readonly seleccionado = signal<number | null>(null);
  private readonly nombresFarmacias = signal<string[]>([]);

  /**
   * las farmacias que se dibujan en el mapa: todas las aprobadas, o solo las que
   * reportaron el medicamento elegido cuando el detalle ya las conoce
   */
  protected readonly farmaciasVisibles = computed((): FarmaciaMapa[] => {
    const todas: FarmaciaMapa[] = this.farmacias();
    const nombres: string[] = this.nombresFarmacias();
    if (this.seleccionado() === null || nombres.length === 0) {
      return todas;
    }
    const reportaron: Set<string> = new Set(nombres);
    const encontradas: FarmaciaMapa[] = todas.filter((farmacia: FarmaciaMapa): boolean =>
      reportaron.has(farmacia.nombre),
    );
    return encontradas.length > 0 ? encontradas : todas;
  });

  constructor() {
    this.farmaciaService
      .listarAprobadas()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (farmacias: FarmaciaMapa[]): void => this.farmacias.set(farmacias),
        error: (): void => this.farmacias.set([]),
      });
  }

  protected seleccionar(medicamentoId: number): void {
    this.seleccionado.set(medicamentoId);
    this.nombresFarmacias.set([]);
  }

  protected cerrarDetalle(): void {
    this.seleccionado.set(null);
    this.nombresFarmacias.set([]);
  }

  protected actualizarFarmaciasReportadas(nombres: string[]): void {
    this.nombresFarmacias.set(nombres);
  }

  protected cerrarSesion(): void {
    this.sesionService.cerrarSesion();
    void this.router.navigateByUrl("/");
  }
}
