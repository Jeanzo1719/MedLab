/**
 * Configuración de entorno de desarrollo.
 *
 * Qué es: los valores de desarrollo; lo usan `ng serve` y `pnpm tauri dev` en
 * lugar de environment.ts.
 *
 * Para qué sirve: apunta a la API local en el puerto 8080.
 */
export const environment: Readonly<{ apiUrl: string }> = {
  apiUrl: "http://localhost:8080/api",
};
