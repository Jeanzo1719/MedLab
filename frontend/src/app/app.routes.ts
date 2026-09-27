import { Routes } from "@angular/router";

import { LandingComponent } from "./features/landing/landing.component";

export const routes: Routes = [
  // Public route: no auth guard, visitors can browse without an account
  {
    path: "",
    component: LandingComponent,
    title: "MedLab | Disponibilidad de medicamentos en farmacias cercanas",
  },
  { path: "**", redirectTo: "" },
];
