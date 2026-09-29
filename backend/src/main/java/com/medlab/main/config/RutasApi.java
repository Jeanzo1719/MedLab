package com.medlab.main.config;

/**
 * Base paths of the REST API, in one place.
 * <p>
 * Controllers use these constants in their {@code @RequestMapping} and {@link CorsConfig} uses them to decide which
 * paths accept cross-origin requests, so renaming a path is a one-line change that cannot leave the controllers and the
 * CORS rules out of sync.
 */
public final class RutasApi {

	/** Public area: no authentication needed (visitor mode) */
	public static final String PUBLICA = "/api/public";

	public static final String FARMACIAS_PUBLICAS = PUBLICA + "/farmacias";
	public static final String MEDICAMENTOS_PUBLICOS = PUBLICA + "/medicamentos";

	private RutasApi() {
		// constants only, never instantiated
	}
}
