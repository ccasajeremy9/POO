-- CelMax: script completo (tablas + datos de prueba)
-- Generado a partir de CelMax/src/main/resources/schema_celmax.sql y data_celmax.sql

-- CelMax - esquema H2 (idempotente: se puede ejecutar en cada arranque)
-- Basado en el modelo db/trabajo_celmax.architect

CREATE TABLE IF NOT EXISTS Categoria (
    idCategoria VARCHAR(10) NOT NULL PRIMARY KEY,
    nombre      VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS Perfil (
    idPerfil     VARCHAR(10) NOT NULL PRIMARY KEY,
    nombrePerfil VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS Usuario (
    idUsuario     VARCHAR(10) NOT NULL PRIMARY KEY,
    nombreUsuario VARCHAR(30) NOT NULL UNIQUE,
    contrasena    VARCHAR(30) NOT NULL,
    idPerfil      VARCHAR(10) NOT NULL,
    CONSTRAINT Perfil_Usuario_fk FOREIGN KEY (idPerfil) REFERENCES Perfil (idPerfil)
);

CREATE TABLE IF NOT EXISTS Cliente (
    idCliente VARCHAR(10) NOT NULL PRIMARY KEY,
    dni       CHAR(8)     NOT NULL UNIQUE,
    nombre    VARCHAR(60) NOT NULL,
    celular   CHAR(9)     NOT NULL,
    correo    VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS Celular (
    idCelular      VARCHAR(10) NOT NULL PRIMARY KEY,
    modelo         VARCHAR(40) NOT NULL,
    marca          VARCHAR(30) NOT NULL,
    almacenamiento INTEGER     NOT NULL,
    ram            INTEGER     NOT NULL,
    idCategoria    VARCHAR(10) NOT NULL,
    CONSTRAINT Categoria_Celular_fk FOREIGN KEY (idCategoria) REFERENCES Categoria (idCategoria)
);

CREATE TABLE IF NOT EXISTS Accesorio (
    idAccesorio VARCHAR(10)   NOT NULL PRIMARY KEY,
    nombre      VARCHAR(60)   NOT NULL,
    tipo        VARCHAR(30)   NOT NULL,
    precio      DECIMAL(10,2) NOT NULL,
    stock       INTEGER       NOT NULL,
    stockMinimo INTEGER       NOT NULL,
    idCategoria VARCHAR(10)   NOT NULL,
    CONSTRAINT Categoria_Accesorio_fk FOREIGN KEY (idCategoria) REFERENCES Categoria (idCategoria)
);

CREATE TABLE IF NOT EXISTS Sucursal (
    idSucursal   VARCHAR(10) NOT NULL PRIMARY KEY,
    numeroTienda INTEGER     NOT NULL,
    ciudad       VARCHAR(40) NOT NULL,
    tipo         VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS Inventario (
    idInventario VARCHAR(10)   NOT NULL PRIMARY KEY,
    idCelular    VARCHAR(10)   NOT NULL,
    idSucursal   VARCHAR(10)   NOT NULL,
    fechaIngreso DATE          NOT NULL,
    stock        INTEGER       NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    CONSTRAINT Celular_Inventario_fk  FOREIGN KEY (idCelular)  REFERENCES Celular (idCelular),
    CONSTRAINT Sucursal_Inventario_fk FOREIGN KEY (idSucursal) REFERENCES Sucursal (idSucursal)
);

CREATE TABLE IF NOT EXISTS Venta (
    idVenta    VARCHAR(10)   NOT NULL PRIMARY KEY,
    idCliente  VARCHAR(10)   NOT NULL,
    fechaVenta DATE          NOT NULL,
    pNeto      DECIMAL(10,2) NOT NULL,
    igv        DECIMAL(10,2) NOT NULL,
    pTotal     DECIMAL(10,2) NOT NULL,
    idUsuario  VARCHAR(10)   NOT NULL,
    CONSTRAINT Cliente_Venta_fk FOREIGN KEY (idCliente) REFERENCES Cliente (idCliente),
    CONSTRAINT Usuario_Venta_fk FOREIGN KEY (idUsuario) REFERENCES Usuario (idUsuario)
);

CREATE TABLE IF NOT EXISTS DetalleVenta (
    idDetalle      VARCHAR(10)   NOT NULL PRIMARY KEY,
    idVenta        VARCHAR(10)   NOT NULL,
    idCelular      VARCHAR(10),
    idAccesorio    VARCHAR(10),
    cantidad       INTEGER       NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subtotal       DECIMAL(10,2) NOT NULL,
    CONSTRAINT Venta_DetalleVenta_fk     FOREIGN KEY (idVenta)     REFERENCES Venta (idVenta),
    CONSTRAINT Celular_DetalleVenta_fk   FOREIGN KEY (idCelular)   REFERENCES Celular (idCelular),
    CONSTRAINT Accesorio_DetalleVenta_fk FOREIGN KEY (idAccesorio) REFERENCES Accesorio (idAccesorio),
    -- cada linea es un celular O un accesorio (nunca los dos, nunca ninguno)
    CONSTRAINT DetalleVenta_producto_ck CHECK (
        (idCelular IS NOT NULL AND idAccesorio IS NULL) OR
        (idCelular IS NULL AND idAccesorio IS NOT NULL))
);

-- CelMax - datos de prueba (tomados del Excel TRABAJO_CELMAX)
-- Solo se ejecuta cuando la base esta vacia (ver DatabaseConfig)

INSERT INTO Categoria (idCategoria, nombre) VALUES
    ('CAT-01', 'Celulares'),
    ('CAT-02', 'Accesorios');

INSERT INTO Perfil (idPerfil, nombrePerfil) VALUES
    ('PF-01', 'Administrador'),
    ('PF-02', 'Gerente'),
    ('PF-03', 'Vendedor');

INSERT INTO Usuario (idUsuario, nombreUsuario, contrasena, idPerfil) VALUES
    ('USR-01', 'Isaac',   'Is@c5G#01',   'PF-01'),
    ('USR-02', 'Jeremy',  'Jer*Pro15x',  'PF-02'),
    ('USR-03', 'Bernabe', 'Bern!Ultra7', 'PF-03'),
    ('USR-04', 'Jhoner',  'Jh0n_Fold26', 'PF-03');

INSERT INTO Cliente (idCliente, dni, nombre, celular, correo) VALUES
    ('CLI-001', '45172983', 'Rosa Villanueva Ccori', '987654321', 'rosa.villanueva@example.com'),
    ('CLI-002', '70458316', 'Jorge Ramos Tito',      '986543210', 'jorge.ramos@example.com'),
    ('CLI-003', '61239854', 'Elena Paredes Luque',   '985432109', 'elena.paredes@example.com'),
    ('CLI-004', '73918246', 'Marco Salas Quispe',    '984321098', 'marco.salas@example.com'),
    ('CLI-005', '48620157', 'Lucía Mendoza Cutipa',  '983210987', 'lucia.mendoza@example.com');

INSERT INTO Celular (idCelular, modelo, marca, almacenamiento, ram, idCategoria) VALUES
    ('CEL-001', 'iPhone 17 Pro',    'Apple',    256, 12, 'CAT-01'),
    ('CEL-002', 'Galaxy S25 Ultra', 'Samsung',  256, 12, 'CAT-01'),
    ('CEL-003', 'Xiaomi 15',        'Xiaomi',   256, 12, 'CAT-01'),
    ('CEL-004', 'Pixel 10',         'Google',   128, 12, 'CAT-01'),
    ('CEL-005', 'Edge 60 Pro',      'Motorola', 256, 12, 'CAT-01'),
    ('CEL-006', 'Honor 400',        'Honor',    256,  8, 'CAT-01');

INSERT INTO Accesorio (idAccesorio, nombre, tipo, precio, stock, stockMinimo, idCategoria) VALUES
    ('ACC-001', 'Funda transparente reforzada', 'Protección',  39.90, 60, 15, 'CAT-02'),
    ('ACC-002', 'Mica de vidrio templado',      'Protección',  25.00, 80, 20, 'CAT-02'),
    ('ACC-003', 'Cargador rápido 65W USB-C',    'Carga',       89.90, 35,  8, 'CAT-02'),
    ('ACC-004', 'Cable USB-C a USB-C 1 m',      'Carga',       29.90, 50, 12, 'CAT-02'),
    ('ACC-005', 'Audífonos Bluetooth',          'Audio',      129.00, 25,  6, 'CAT-02'),
    ('ACC-006', 'Power bank 20 000 mAh',        'Carga',      119.00, 18,  5, 'CAT-02');

INSERT INTO Sucursal (idSucursal, numeroTienda, ciudad, tipo) VALUES
    ('SUC-01', 1, 'Lima',     'Flagship'),
    ('SUC-02', 2, 'Arequipa', 'Mall'),
    ('SUC-03', 3, 'Cusco',    'Mall'),
    ('SUC-04', 4, 'Trujillo', 'Quiosco');

INSERT INTO Inventario (idInventario, idCelular, idSucursal, fechaIngreso, stock, precio) VALUES
    ('INV-001', 'CEL-001', 'SUC-01', DATE '2026-10-01', 12, 5499.00),
    ('INV-002', 'CEL-002', 'SUC-02', DATE '2026-10-02', 15, 4999.00),
    ('INV-003', 'CEL-003', 'SUC-01', DATE '2026-10-02', 20, 2999.00),
    ('INV-004', 'CEL-004', 'SUC-03', DATE '2026-10-03', 10, 3299.00),
    ('INV-005', 'CEL-005', 'SUC-04', DATE '2026-10-05', 25, 1799.00),
    ('INV-006', 'CEL-006', 'SUC-02', DATE '2026-10-05', 30, 1399.00);

INSERT INTO Venta (idVenta, idCliente, fechaVenta, pNeto, igv, pTotal, idUsuario) VALUES
    ('VTA-001', 'CLI-001', DATE '2026-10-12', 1206.78, 217.22, 1424.00, 'USR-03'),
    ('VTA-002', 'CLI-002', DATE '2026-10-13', 4693.98, 844.92, 5538.90, 'USR-03'),
    ('VTA-003', 'CLI-003', DATE '2026-10-14', 2592.20, 466.60, 3058.80, 'USR-04'),
    ('VTA-004', 'CLI-004', DATE '2026-10-15', 1600.76, 288.14, 1888.90, 'USR-04'),
    ('VTA-005', 'CLI-005', DATE '2026-10-16', 2905.08, 522.92, 3428.00, 'USR-03');

INSERT INTO DetalleVenta (idDetalle, idVenta, idCelular, idAccesorio, cantidad, precioUnitario, subtotal) VALUES
    ('DET-001', 'VTA-001', 'CEL-006', NULL,      1, 1399.00, 1399.00),
    ('DET-002', 'VTA-001', NULL,      'ACC-002', 1,   25.00,   25.00),
    ('DET-003', 'VTA-002', 'CEL-001', NULL,      1, 5499.00, 5499.00),
    ('DET-004', 'VTA-002', NULL,      'ACC-001', 1,   39.90,   39.90),
    ('DET-005', 'VTA-003', 'CEL-003', NULL,      1, 2999.00, 2999.00),
    ('DET-006', 'VTA-003', NULL,      'ACC-004', 2,   29.90,   59.80),
    ('DET-007', 'VTA-004', 'CEL-005', NULL,      1, 1799.00, 1799.00),
    ('DET-008', 'VTA-004', NULL,      'ACC-003', 1,   89.90,   89.90),
    ('DET-009', 'VTA-005', 'CEL-004', NULL,      1, 3299.00, 3299.00),
    ('DET-010', 'VTA-005', NULL,      'ACC-005', 1,  129.00,  129.00);
