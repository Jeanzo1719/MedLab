import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
} from "@angular/core";
import { provideHttpClient, withFetch } from "@angular/common/http";
import { provideRouter, withInMemoryScrolling } from "@angular/router";

import { routes } from "./app.routes";

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Scrolls to /#section fragments without reloading the page
    provideRouter(
      routes,
      withInMemoryScrolling({
        anchorScrolling: "enabled",
        scrollPositionRestoration: "enabled",
      }),
    ),
    provideHttpClient(withFetch()),
  ],
};
