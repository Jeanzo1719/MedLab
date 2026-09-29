package com.medlab.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend de MedLab (Spring Boot).
 * <p>
 * Qué es: la clase que arranca la JVM ({@code java -jar app.jar} en Docker, {@code mvn spring-boot:run} en local).
 * <p>
 * Cómo funciona: {@code @SpringBootApplication} activa la autoconfiguración y escanea este paquete y sus subpaquetes
 * ({@code config}, {@code controller}, {@code service}, {@code mapper}, {@code repository}, {@code exception}), así que
 * cada componente de la arquitectura por capas se registra sin configuración manual.
 * <p>
 * Para qué sirve: levanta el servidor web embebido que expone la API REST que consume el frontend Angular/Tauri.
 */
@SpringBootApplication
public class MedlabApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedlabApplication.class, args);
	}

}
