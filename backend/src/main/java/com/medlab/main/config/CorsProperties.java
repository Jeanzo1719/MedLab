package com.medlab.main.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

/**
 * Propiedades tipadas de CORS, leídas de las propiedades {@code cors.*} (capa de configuración).
 * <p>
 * Cómo funciona: en local los valores salen de application-dev.yml; en Docker, de la variable de entorno
 * {@code CORS_ORIGENES_PERMITIDOS} (separada por comas), que Spring Boot asocia con {@code cors.origenes-permitidos}.
 * La lista se valida al arrancar: si falta o está vacía, el backend no inicia y explica el motivo, en lugar de
 * funcionar sin orígenes permitidos.
 * <p>
 * Para qué sirve: {@link CorsConfig} la recibe por inyección, así la configuración queda en un solo lugar, tipada y
 * documentada.
 *
 * @param origenesPermitidos orígenes que pueden llamar a la API pública, por ejemplo {@code http://localhost:1420}
 */
@Validated
@ConfigurationProperties("cors")
public record CorsProperties(@NotEmpty List<String> origenesPermitidos) {
}
