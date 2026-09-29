package com.medlab.main.config;

/**
 * Rutas base de la API REST, en un solo lugar (capa de configuración).
 * <p>
 * Para qué sirve: si una ruta cambia, basta con cambiar una línea, y los controllers y las reglas de CORS no pueden
 * quedar desincronizados.
 * <p>
 * Cómo funciona: los controllers usan estas constantes en su {@code @RequestMapping} y {@link CorsConfig} las usa para
 * decidir qué rutas aceptan peticiones de otros orígenes. El frontend tiene su equivalente en
 * {@code core/api/api-rutas.ts}.
 */
public final class RutasApi {

	/** Área pública: no requiere autenticación (modo visitante) */
	public static final String PUBLICA = "/api/public";

	public static final String FARMACIAS_PUBLICAS = PUBLICA + "/farmacias";
	public static final String MEDICAMENTOS_PUBLICOS = PUBLICA + "/medicamentos";

	private RutasApi() {
		// solo constantes: nunca se instancia
	}
}
