import { Routes } from "@angular/router";

import { redirigirAutenticadoGuard } from "./core/guards/redirigir-autenticado.guard";
import { FarmaciasCercanasComponent } from "./features/farmacias-cercanas/farmacias-cercanas.component";
import { LandingComponent } from "./features/landing/landing.component";

export const routes: Routes = [
  {
    path: "",
    component: LandingComponent,
    title: "MedLab | Disponibilidad de medicamentos en farmacias cercanas",
    canActivate: [redirigirAutenticadoGuard],
  },
  {
    path: "farmacias-cercanas",
    component: FarmaciasCercanasComponent,
    title: "MedLab | Farmacias cercanas con tu medicamento",
  },
  { path: "**", redirectTo: "" },
];
