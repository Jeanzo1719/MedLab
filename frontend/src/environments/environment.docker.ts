/**
 *
 * la configuración de la imagen de Docker (pnpm ng build --configuration docker)
 *
 * el proxy inverso publica el frontend y el backend bajo el mismo origen, así que
 * la API y el WebSocket se piden a la dirección que el navegador ya usa. Así la
 * misma imagen sirve en cualquier dominio, sin recompilar
 *
 */
export const environment: Readonly<{
  wsUrl: string;
  apiUrl: string;
}> = {
  /**
   *
   * el WebSocket usa wss:// cuando la página se abre con https://
   *
   */
  wsUrl: `${location.protocol === "https:" ? "wss" : "ws"}://${location.host}/ws`,

  /**
   *
   * la API REST bajo el mismo origen que la página
   *
   */
  apiUrl: `${location.origin}/api`,
};
