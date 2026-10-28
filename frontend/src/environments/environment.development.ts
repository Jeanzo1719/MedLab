/**
 * 
 * la configuración de desarrollo, la que se usa mientras se programa en local
 * 
 * al correr pnpm ng serve, Angular usa este archivo en lugar de environment.ts
 * (configurado en angular.json), así que el código no necesita saber cuál está activo
 * 
 */
export const environment: Readonly<{
  wsUrl: string;
  apiUrl: string;
  farmaciaIdTemporal: number;
  adminIdTemporal: number;
}> = {
  /**
   * 
   * la dirección del WebSocket del backend corriendo en la máquina local
   * (localhost:8080), adonde se conecta el frontend para pedir los datos
   * 
   */
  wsUrl: "ws://localhost:8080/ws",

  /**
   * 
   * la dirección de la API REST del backend local, la que usa el panel de la farmacia
   * 
   */
  apiUrl: "http://localhost:8080/api",

  /**
   * 
   * TEMPORAL: el id de la farmacia que usa el panel, porque todavía no hay login (HU-2).
   * 9001 es la farmacia de prueba de la base de datos local
   * 
   */
  farmaciaIdTemporal: 9001,

  /**
   * 
   * TEMPORAL: el id del administrador que usa su panel, porque todavía no hay login (HU-2).
   * 1 es el administrador de la base de datos local
   * 
   */
  adminIdTemporal: 1,
};