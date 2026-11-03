-- Índices de apoyo para la búsqueda combinada de la HU-3.
--
-- La consulta filtra por farmacia aprobada y por rango de precio, y agrupa por
-- medicamento. Las llaves foráneas ya crean un índice por columna, pero estos
-- índices compuestos permiten resolver el filtro y ordenar sin recorrer la
-- tabla completa.

CREATE INDEX idx_disponibilidad_medicamento_precio
    ON disponibilidad (medicamento_id, precio);

CREATE INDEX idx_farmacias_estado_nombre
    ON farmacias (estado_aprobacion_id, nombre_farmacia);
