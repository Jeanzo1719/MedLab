// TODO: reemplazar por las URL de la API de producción cuando el backend esté desplegado
/**
 * 
 * la configuración de producción, la que se usa al compilar la versión final
 * (pnpm ng build)
 * 
 * el resto del código siempre importa este archivo; al correr pnpm ng serve,
 * Angular lo reemplaza por environment.development.ts (configurado en angular.json)
 * 
 */
export const environment: Readonly<{ wsUrl: string; apiUrl: string; farmaciaIdTemporal: number }> = {
  /**
   * 
   * la dirección del WebSocket del backend, adonde se conecta el frontend para
   * pedir los datos. ws:// es como http:// pero para WebSocket, y /ws es la
   * puerta que abre WebSocketConfig en el backend
   * 
   */
  wsUrl: "ws://localhost:8080/ws",

  /**
   * 
   * la dirección de la API REST del backend, la que usa el panel de la farmacia
   * 
   */
  apiUrl: "http://localhost:8080/api",

  /**
   * 
   * TEMPORAL: el id de la farmacia que usa el panel, porque todavía no hay login (HU-2).
   * Cuando exista, esto desaparece y la farmacia sale de la sesión
   * 
   */
  farmaciaIdTemporal: 9001,
};