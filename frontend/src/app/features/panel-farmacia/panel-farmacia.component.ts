import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";

/**
 * 
 * el panel de la farmacia: la barra de arriba con las pestañas y, debajo, la
 * sección que esté abierta
 * 
 * no carga datos; cada sección lo hace por su cuenta. Es una ruta con hijas
 * (/farmacia/resumen y /farmacia/inventario), definidas en app.routes.ts
 * 
 */
@Component({
  selector: "app-panel-farmacia",
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: "./panel-farmacia.component.html",
  styleUrl: "./panel-farmacia.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelFarmaciaComponent {}