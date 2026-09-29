import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideHttpClient, withFetch } from "@angular/common/http";
import { provideRouter, withInMemoryScrolling } from "@angular/router";

import { routes } from "./app.routes";
import { MetadatosService } from "./core/services/metadatos.service";

/**
 * Configuración global de la aplicación.
 *
 * Qué es: la lista de providers con la que main.ts arranca Angular.
 *
 * Cómo funciona: registra el router con scroll a anclas (para las secciones de
 * la landing), HttpClient con fetch (para llamar a la API) y un inicializador
 * que aplica los metadatos generales del documento.
 *
 * Para qué sirve: concentra en un solo lugar todo lo que la app necesita antes
 * de mostrar la primera página.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Desplaza a los fragmentos /#seccion sin recargar la página
    provideRouter(
      routes,
      withInMemoryScrolling({
        anchorScrolling: "enabled",
        scrollPositionRestoration: "enabled",
      }),
    ),
    provideHttpClient(withFetch()),
    // Metadatos generales del documento (idioma, color de tema, favicon), una sola vez al arrancar
    provideAppInitializer(() => inject(MetadatosService).aplicarGenerales()),
  ],
};
