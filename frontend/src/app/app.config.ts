import { provideHttpClient } from "@angular/common/http";
import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideRouter, withInMemoryScrolling } from "@angular/router";

import { routes } from "./app.routes";
import { MetadatosService } from "./core/services/metadatos.service";
import { TemaService } from "./core/services/tema.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
    provideRouter(
      routes,
      withInMemoryScrolling({
        anchorScrolling: "enabled",
        scrollPositionRestoration: "enabled",
      }),
    ),
    provideAppInitializer(() => inject(MetadatosService).aplicarGenerales()),
    provideAppInitializer(() => inject(TemaService).iniciar()),
  ],
};