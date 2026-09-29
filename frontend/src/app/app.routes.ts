import { Routes } from "@angular/router";

import { redirigirAutenticadoGuard } from "./core/guards/redirigir-autenticado.guard";
import { LandingComponent } from "./features/landing/landing.component";

/**
 * Tabla de rutas de la aplicación.
 *
 * Qué es: el único lugar donde se declaran las URL de la app, como pidió la
 * revisión de la PR.
 *
 * Cómo funciona: el router compara la URL con cada entrada en orden. La ruta
 * vacía es la landing pública, protegida por redirigirAutenticadoGuard, y la
 * ruta comodín devuelve cualquier URL desconocida a la landing.
 *
 * Para qué sirve: define qué página ve el usuario en cada URL y el título de
 * cada una.
 */
export const routes: Routes = [
  // Ruta pública: no requiere autenticación y el visitante navega sin cuenta;
  // quien ya tiene sesión es enviado a su panel
  {
    path: "",
    component: LandingComponent,
    title: "MedLab | Disponibilidad de medicamentos en farmacias cercanas",
    canActivate: [redirigirAutenticadoGuard],
  },
  // Los paneles de cada rol llegan con la HU de autenticación. Cada path debe ser
  // el nombre del rol (paciente, farmacia, administrador), porque
  // redirigirAutenticadoGuard envía al usuario con sesión a "/<rol>". Hay que
  // declararlos aquí antes de que SesionService devuelva sesiones reales; si no,
  // la ruta comodín de abajo devolvería al usuario a la landing.
  { path: "**", redirectTo: "" },
];
