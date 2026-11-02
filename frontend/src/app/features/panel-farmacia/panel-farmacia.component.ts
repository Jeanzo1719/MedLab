import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";

import { SesionService } from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";

/**
 * 
 * el panel de la farmacia: la barra de arriba con las pestañas y, debajo, la
 * sección que esté abierta
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
