import { Routes } from "@angular/router";

import { requiereSesion } from "./core/guards/autenticado.guard";
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
  // Registro (HU-1): se carga bajo demanda, igual que los paneles, para que la
  // landing no pese más por un formulario que no todos abren.
  {
    path: "registro",
    title: "MedLab | Crear cuenta",
    loadComponent: () =>
      import("./features/registro/registro.component").then(
        (modulo) => modulo.RegistroComponent,
      ),
  },
  // Inicio de sesión (HU-2): público; al entrar redirige al panel del rol.
  {
    path: "iniciar-sesion",
    title: "MedLab | Iniciar sesión",
    loadComponent: () =>
      import("./features/iniciar-sesion/iniciar-sesion.component").then(
        (modulo) => modulo.IniciarSesionComponent,
      ),
  },
  // Panel del paciente (HU-2): protegido; tiene la bienvenida y el buscador de medicamentos.
  {
    path: "paciente",
    title: "MedLab | Mi cuenta",
    canActivate: [requiereSesion("paciente")],
    loadComponent: () =>
      import("./features/panel-paciente/panel-paciente.component").then(
        (modulo) => modulo.PanelPacienteComponent,
      ),
  },
  // Se carga bajo demanda para que la landing no pese más por el panel.
  {
    path: "farmacia",
    title: "MedLab | Panel de la farmacia",
    canActivate: [requiereSesion("farmacia")],
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
  // Misma idea que /farmacia: se carga bajo demanda para no pesar en la landing.
  {
    path: "administrador",
    title: "MedLab | Panel del administrador",
    canActivate: [requiereSesion("administrador")],
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
        path: "solicitudes",
        title: "MedLab | Solicitudes de farmacias",
        loadComponent: () =>
          import("./features/panel-admin/solicitudes/solicitudes-admin.component").then(
            (modulo) => modulo.SolicitudesAdminComponent,
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