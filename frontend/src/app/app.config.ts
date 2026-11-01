import { provideHttpClient, withInterceptors } from "@angular/common/http";
import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideRouter, withInMemoryScrolling } from "@angular/router";

import { routes } from "./app.routes";
import { authInterceptor } from "./core/interceptors/auth.interceptor";
import { MetadatosService } from "./core/services/metadatos.service";
import { TemaService } from "./core/services/tema.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor])),
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