/**
 * 
 * la configuración de desarrollo, la que se usa mientras se programa en local
 * 
 * al correr pnpm ng serve, Angular usa este archivo en lugar de environment.ts
 * (configurado en angular.json), así que el código no necesita saber cuál está activo
 * 
 */
export const environment: Readonly<{ wsUrl: string }> = {
  /**
   * 
   * la dirección del WebSocket del backend corriendo en la máquina local
   * (localhost:8080), adonde se conecta el frontend para pedir los datos
   * 
   */
  wsUrl: "ws://localhost:8080/ws",
};
