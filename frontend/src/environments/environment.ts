// TODO: reemplazar por la URL de la API de producción cuando el backend esté desplegado
/**
 * Configuración de entorno de producción.
 *
 * Qué es: los valores que cambian según dónde corre la app; lo usan `ng build` y
 * `pnpm tauri build`.
 *
 * Cómo funciona: angular.json reemplaza este archivo por
 * environment.development.ts al usar `ng serve` o `pnpm tauri dev`.
 *
 * Para qué sirve: core/api/api-rutas.ts toma de aquí la URL base de la API.
 */
export const environment: Readonly<{ apiUrl: string }> = {
  apiUrl: "http://localhost:8080/api",
};
