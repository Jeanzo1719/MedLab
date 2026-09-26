CREATE TABLE roles (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(30)     NOT NULL UNIQUE
);

CREATE TABLE estados_farmacia (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(30)     NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    identificacion      VARCHAR(10)     NOT NULL UNIQUE,
    correo              VARCHAR(150)    NOT NULL,
    contrasena_hash     VARCHAR(255)    NOT NULL,
    telefono            VARCHAR(20)     NOT NULL,
    rol_id              INT             NOT NULL,
    fecha_registro      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (rol_id) REFERENCES roles(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CHECK (identificacion REGEXP '^[0-9]{6,10}$'),
    CHECK (correo REGEXP '^[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}$')
);

CREATE TABLE pacientes (
    usuario_id          INT             PRIMARY KEY,
    primer_nombre       VARCHAR(50)     NOT NULL,
    segundo_nombre      VARCHAR(100)    NULL,
    apellidos           VARCHAR(100)    NOT NULL,
    eps_afiliada        VARCHAR(100)    NOT NULL,

    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE farmacias (
    usuario_id                          INT             PRIMARY KEY,
    nombre_farmacia                     VARCHAR(150)    NOT NULL,

    tipo_via_principal                  VARCHAR(20)     NOT NULL,
    numero_via_principal                VARCHAR(10)     NOT NULL,
    letra_via_principal                 VARCHAR(5)      NULL,
    numero_via_generadora               VARCHAR(10)     NOT NULL,
    letra_via_generadora                VARCHAR(5)      NULL,
    numero_predio                       VARCHAR(10)     NOT NULL,
    complemento                         VARCHAR(100)    NULL,
    horario_atencion                    VARCHAR(100)    NULL,

    nit                                 VARCHAR(20)     NOT NULL,
    rut                                 VARCHAR(20)     NOT NULL,
    matricula_mercantil                 VARCHAR(20)     NOT NULL,
    identificacion_director_tecnico     VARCHAR(10)     NOT NULL,
    licencia_sanitaria                  VARCHAR(50)     NOT NULL,

    latitud                             DECIMAL(10, 8)  NOT NULL,
    longitud                            DECIMAL(11, 8)  NOT NULL,

    estado_aprobacion_id                INT             NOT NULL,
    fecha_ultima_actualizacion          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (estado_aprobacion_id) REFERENCES estados_farmacia(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CHECK (matricula_mercantil REGEXP '^[A-Za-z0-9]{5,20}$'),
    CHECK (identificacion_director_tecnico REGEXP '^[0-9]{6,10}$'),

    INDEX idx_farmacias_ubicacion (latitud, longitud)
);

CREATE TABLE sesiones (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    usuario_id          INT             NOT NULL,
    token               VARCHAR(500)    NOT NULL UNIQUE,
    fecha_inicio        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP,
    fecha_expiracion    DATETIME        NOT NULL,
    fecha_cierre        DATETIME        NULL,
    activa              BOOLEAN         NOT NULL DEFAULT TRUE,

    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    INDEX idx_sesiones_usuario_activa (usuario_id, activa)
);

CREATE TABLE alertas_inactividad_farmacia (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    farmacia_id         INT             NOT NULL,
    fecha_generada      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atendida            BOOLEAN         NOT NULL DEFAULT FALSE,

    FOREIGN KEY (farmacia_id) REFERENCES farmacias(usuario_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE auditoria_estados_farmacia (
    id                    INT             AUTO_INCREMENT PRIMARY KEY,
    farmacia_id           INT             NOT NULL,
    usuario_admin_id      INT             NULL,
    estado_anterior_id    INT             NOT NULL,
    estado_nuevo_id       INT             NOT NULL,
    fecha                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (farmacia_id) REFERENCES farmacias(usuario_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (usuario_admin_id) REFERENCES usuarios(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (estado_anterior_id) REFERENCES estados_farmacia(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (estado_nuevo_id) REFERENCES estados_farmacia(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_auditoria_farmacia_fecha (farmacia_id, fecha)
);

CREATE TABLE categorias (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL UNIQUE
);

CREATE TABLE formas_farmaceuticas (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(30)     NOT NULL UNIQUE
);

CREATE TABLE medicamentos (
    id                      INT             AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial        VARCHAR(100)    NOT NULL,
    principio_activo        VARCHAR(150)    NOT NULL,
    categoria_id            INT             NOT NULL,
    presentacion            VARCHAR(100)    NOT NULL,
    forma_farmaceutica_id   INT             NOT NULL,

    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (forma_farmaceutica_id) REFERENCES formas_farmaceuticas(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CHECK (CHAR_LENGTH(nombre_comercial) BETWEEN 2 AND 100),
    CHECK (CHAR_LENGTH(principio_activo) BETWEEN 2 AND 150),

    UNIQUE (nombre_comercial, principio_activo, presentacion, forma_farmaceutica_id),
    INDEX idx_medicamentos_nombre_comercial (nombre_comercial),
    INDEX idx_medicamentos_principio_activo (principio_activo)
);

CREATE TABLE estados_disponibilidad (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(30)     NOT NULL UNIQUE
);

INSERT INTO estados_disponibilidad (nombre) VALUES
    ('disponible'),
    ('agotado'),
    ('bajo_pedido');

CREATE TABLE disponibilidad (
    id                          INT             AUTO_INCREMENT PRIMARY KEY,
    farmacia_id                 INT             NOT NULL,
    medicamento_id              INT             NOT NULL,
    cantidad_disponible         INT             NOT NULL,
    precio                      DECIMAL(10, 2)  NOT NULL,
    estado_disponibilidad_id    INT             NOT NULL,
    fecha_hora_reporte          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (farmacia_id) REFERENCES farmacias(usuario_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (estado_disponibilidad_id) REFERENCES estados_disponibilidad(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CHECK (cantidad_disponible >= 0),
    CHECK (precio > 0),

    UNIQUE (farmacia_id, medicamento_id),
    INDEX idx_disponibilidad_precio (precio)
);

CREATE TABLE tipos_calificacion (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(30)     NOT NULL UNIQUE
);

CREATE TABLE calificaciones (
    id                      INT             AUTO_INCREMENT PRIMARY KEY,
    disponibilidad_id       INT             NOT NULL,
    usuario_id              INT             NOT NULL,
    tipo_calificacion_id    INT             NOT NULL,
    fecha_hora              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (disponibilidad_id) REFERENCES disponibilidad(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (tipo_calificacion_id) REFERENCES tipos_calificacion(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    UNIQUE (disponibilidad_id, usuario_id),
    INDEX idx_calificaciones_disponibilidad_fecha (disponibilidad_id, fecha_hora)
);
