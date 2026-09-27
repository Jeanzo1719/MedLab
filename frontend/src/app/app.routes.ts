import { Routes } from "@angular/router";

import { redirigirAutenticadoGuard } from "./core/guards/redirigir-autenticado.guard";
import { LandingComponent } from "./features/landing/landing.component";

export const routes: Routes = [
  // Public route: no auth required, visitors browse without an account;
  // users who already have a session are sent to their panel instead
  {
    path: "",
    component: LandingComponent,
    title: "MedLab | Disponibilidad de medicamentos en farmacias cercanas",
    canActivate: [redirigirAutenticadoGuard],
  },
  { path: "**", redirectTo: "" },
];
