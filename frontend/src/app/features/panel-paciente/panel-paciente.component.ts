import { ChangeDetectionStrategy, Component, Signal, inject } from "@angular/core";
import { Router, RouterLink } from "@angular/router";

import { Sesion, SesionService } from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";
import { BuscadorMedicamentosComponent } from "../../shared/components/buscador-medicamentos/buscador-medicamentos.component";

/**
 *
 * el panel del paciente/cuidador: la bienvenida y el buscador de medicamentos. Con
 * sesión iniciada la landing redirige aquí, así que este es su punto de partida para
 * buscar un medicamento, ver las farmacias cercanas y calificar
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
