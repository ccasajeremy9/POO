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
