package com.medlab.main.entity;

/**
 * Estado de aprobación de una farmacia (capa de entidades).
 * <p>
 * Para qué sirve: solo las farmacias {@link #APROBADA} se muestran a los visitantes; las {@link #PENDIENTE} esperan la
 * revisión de un administrador y las {@link #SUSPENDIDA} fueron dadas de baja.
 * <p>
 * Cómo funciona: va embebido en el documento de la farmacia en lugar de vivir en una colección aparte, según el modelo
 * de documentos de MongoDB, así que filtrar por estado no necesita ningún join.
 */
public enum EstadoAprobacion {
	PENDIENTE, APROBADA, SUSPENDIDA
}
