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
  // TODO(HU-2): proteger /farmacia con un guard que exija sesión con rol "farmacia".
  // Se carga bajo demanda para que la landing no pese más por el panel.
  {
    path: "farmacia",
    title: "MedLab | Panel de la farmacia",
    loadComponent: () =>
      import("./features/panel-farmacia/panel-farmacia.component").then(
        (modulo) => modulo.PanelFarmaciaComponent,
      ),
    children: [
      { path: "", pathMatch: "full", redirectTo: "resumen" },
      {
        path: "resumen",
        title: "MedLab | Resumen del inventario",
        loadComponent: () =>
          import("./features/panel-farmacia/resumen/resumen-farmacia.component").then(
            (modulo) => modulo.ResumenFarmaciaComponent,
          ),
      },
      {
        path: "inventario",
        title: "MedLab | Inventario",
        loadComponent: () =>
          import("./features/panel-farmacia/inventario/inventario-farmacia.component").then(
            (modulo) => modulo.InventarioFarmaciaComponent,
          ),
      },
      {
        path: "datos",
        title: "MedLab | Mi farmacia",
        loadComponent: () =>
          import("./features/panel-farmacia/datos/datos-farmacia.component").then(
            (modulo) => modulo.DatosFarmaciaComponent,
          ),
      },
    ],
  },
  // TODO(HU-2): proteger /administrador con un guard que exija sesión con rol "administrador".
  // Misma idea que /farmacia: se carga bajo demanda para no pesar en la landing.
  {
    path: "administrador",
    title: "MedLab | Panel del administrador",
    loadComponent: () =>
      import("./features/panel-admin/panel-admin.component").then(
        (modulo) => modulo.PanelAdminComponent,
      ),
    children: [
      { path: "", pathMatch: "full", redirectTo: "usuarios" },
      {
        path: "usuarios",
        title: "MedLab | Usuarios",
        loadComponent: () =>
          import("./features/panel-admin/usuarios/usuarios-admin.component").then(
            (modulo) => modulo.UsuariosAdminComponent,
          ),
      },
      {
        path: "farmacias",
        title: "MedLab | Farmacias",
        loadComponent: () =>
          import("./features/panel-admin/farmacias/farmacias-admin.component").then(
            (modulo) => modulo.FarmaciasAdminComponent,
          ),
      },
      {
        path: "alertas",
        title: "MedLab | Alertas de inactividad",
        loadComponent: () =>
          import("./features/panel-admin/alertas/alertas-admin.component").then(
            (modulo) => modulo.AlertasAdminComponent,
          ),
      },
      {
        path: "calificaciones",
        title: "MedLab | Calificaciones sospechosas",
        loadComponent: () =>
          import("./features/panel-admin/calificaciones/calificaciones-admin.component").then(
            (modulo) => modulo.CalificacionesAdminComponent,
          ),
      },
    ],
  },
  { path: "**", redirectTo: "" },
];