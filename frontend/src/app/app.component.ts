import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";

/**
 * Componente raíz de la aplicación (shell).
 *
 * Qué es: el componente <app-root> que carga index.html.
 *
 * Cómo funciona: su plantilla solo tiene <router-outlet />, donde el router
 * dibuja la página que corresponde a la URL según app.routes.ts.
 *
 * Para qué sirve: es el contenedor de todas las páginas. A propósito no tiene
 * lógica: la configuración global vive en app.config.ts.
 */
@Component({
  selector: "app-root",
  imports: [RouterOutlet],
  templateUrl: "./app.component.html",
})
export class AppComponent {}
