INSERT INTO estados_farmacia (nombre) VALUES
    ('pendiente'),
    ('aprobada'),
    ('suspendida');


/**
 * miracion de flyway que crea y actualiza la base de datos cuando arranca el back end.
 * carga los estados disponibles de una farmacia en la tabla estados_farmacia:
 *
 * Por qué hace falta: V1 solo crea la tabla vacía. Cada farmacia apunta a uno de estos
 * estados con la columna estado_aprobacion_id, así que sin estas filas no se puede registrar
 * ninguna farmacia. El backend busca las aprobadas por el nombre 'aprobada', por lo que ese
 * texto tiene que existir exactamente así.
 */
