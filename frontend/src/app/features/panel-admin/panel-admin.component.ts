import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";

/**
 * 
 * el panel del administrador: la barra de arriba con las pestañas y, debajo, la
 * sección que esté abierta
 * 
 * no carga datos; cada sección lo hace por su cuenta. Es una ruta con hijas
 * (/administrador/usuarios, etc.), definidas en app.routes.ts
 * 
 */
@Component({
  selector: "app-panel-admin",
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: "./panel-admin.component.html",
  styleUrl: "./panel-admin.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelAdminComponent {}