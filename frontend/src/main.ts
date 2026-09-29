import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { appConfig } from "./app/app.config";

/**
 * Punto de entrada del frontend.
 *
 * Qué es: el primer archivo que ejecuta el navegador (o la ventana de Tauri).
 *
 * Cómo funciona: arranca el componente raíz AppComponent con la configuración
 * global de app.config.ts (router, HttpClient y tareas de arranque).
 *
 * Para qué sirve: pone en marcha la aplicación Angular; no contiene lógica
 * propia.
 */
bootstrapApplication(AppComponent, appConfig).catch((err) =>
  console.error(err),
);
