import { ChangeDetectionStrategy, Component, Signal, inject } from "@angular/core";
import { Router, RouterLink } from "@angular/router";

import { Sesion, SesionService } from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";
import { BuscadorMedicamentosComponent } from "../../shared/components/buscador-medicamentos/buscador-medicamentos.component";

/**
 *
 * el panel del paciente/cuidador: por ahora una bienvenida con los accesos que ya
 * existen. Las historias siguientes le irán añadiendo secciones
 *
 */
@Component({
  selector: "app-panel-paciente",
  imports: [RouterLink, BotonTemaComponent, BuscadorMedicamentosComponent],
  templateUrl: "./panel-paciente.component.html",
  styleUrl: "./panel-paciente.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelPacienteComponent {
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly router: Router = inject(Router);

  protected readonly sesion: Signal<Sesion | null> = this.sesionService.sesionActual;

  protected cerrarSesion(): void {
    this.sesionService.cerrarSesion();
    void this.router.navigateByUrl("/");
  }
}
