package com.medlab.main.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

/**
 * Typed CORS settings, bound from the {@code cors.*} properties.
 * <p>
 * Locally the values come from application-dev.yml; in Docker from the {@code CORS_ORIGENES_PERMITIDOS} environment
 * variable (comma-separated), which Spring Boot maps to {@code cors.origenes-permitidos}. The list is validated when
 * the application starts: if it is missing or empty the backend refuses to start with a clear message, instead of
 * running with no allowed origins.
 *
 * @param origenesPermitidos origins allowed to call the public API, e.g. {@code http://localhost:1420}
 */
@Validated
@ConfigurationProperties("cors")
public record CorsProperties(@NotEmpty List<String> origenesPermitidos) {
}
