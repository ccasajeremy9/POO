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
