import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";

import { SesionService } from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";

/**
 * 
 * el panel del administrador: la barra de arriba con las pestañas y, debajo, la
 * sección que esté abierta
 * 
 * no carga datos; cada sección lo hace por su cuenta. Es una ruta con hijas
 * (/administrador/usuarios, etc.), definidas en app.routes.ts. El guard de la
 * ruta exige una sesión con rol "administrador"
 * 
 */
@Component({
  selector: "app-panel-admin",
  imports: [RouterLink, RouterLinkActive, RouterOutlet, BotonTemaComponent],
  templateUrl: "./panel-admin.component.html",
  styleUrl: "./panel-admin.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelAdminComponent {
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly router: Router = inject(Router);

  /**
   * 
   * cierra la sesión y vuelve a la landing
   * 
   */
  protected cerrarSesion(): void {
    this.sesionService.cerrarSesion();
    void this.router.navigateByUrl("/");
  }
}
