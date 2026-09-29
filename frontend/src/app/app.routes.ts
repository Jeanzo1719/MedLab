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
  // Role panels arrive with the authentication user story. Each path must be the
  // role's name (paciente, farmacia, administrador), because redirigirAutenticadoGuard
  // sends a logged-in user to "/<rol>"; declare them here before SesionService
  // returns real sessions, or the wildcard below would bounce users back to the landing.
  { path: "**", redirectTo: "" },
];
