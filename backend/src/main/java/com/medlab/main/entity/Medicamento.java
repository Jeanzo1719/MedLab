package com.medlab.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Medicamento del modelo de dominio (capa de entidades).
 * <p>
 * Para qué sirve: es la representación interna de un medicamento, tal como se guardará en la colección "medicamentos"
 * de MongoDB. La API nunca lo devuelve directamente: {@link com.medlab.main.mapper.MedicamentoMapper} lo convierte en
 * el DTO público.
 * <p>
 * Cómo funciona: es una clase simple con getters, setters y constructores generados por Lombok. La categoría y la forma
 * farmacéutica van embebidas como texto en lugar de tablas de consulta, según el modelo de documentos de MongoDB.
 * Cuando se conecte MongoDB se anotará con {@code @Document}; los pasos están en el TODO de
 * {@link com.medlab.main.repository.MedicamentoRepositoryMock}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

	private String id;
	private String nombreComercial;
	private String principioActivo;
	/** Categoría terapéutica, por ejemplo "Analgésico"; no forma parte del resultado público de búsqueda */
	private String categoria;
	/** Presentación del empaque, por ejemplo "500 mg x 20" */
	private String presentacion;
	/** Forma farmacéutica, por ejemplo "Tableta" o "Jarabe" */
	private String formaFarmaceutica;
}
