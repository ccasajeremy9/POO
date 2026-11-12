# Base de datos CelMax

- **Motor:** H2 2.4 embebido en archivo (`jdbc:h2:file:./data/celmax`), igual que SysVentas.
- **Pool de conexiones:** HikariCP.
- **Modelo:** `trabajo_celmax.architect` (SQL Power Architect).
- **Script:** `celmax.sql` (copia de `schema_celmax.sql` + `data_celmax.sql` que usa la app).

La aplicación crea las tablas al arrancar (`CREATE TABLE IF NOT EXISTS`) y carga los
datos de prueba **solo si la base está vacía**, así no se pierden las ventas ni el stock
entre ejecuciones. Para empezar de cero basta con borrar la carpeta `CelMax/data/`.

## Diagrama de relaciones

```
Perfil 1 ──< Usuario 1 ──< Venta >── 1 Cliente
                             │
                             1
                             │
                             ^
                        DetalleVenta >── 0..1 Celular  >── 1 Categoria
                             │                  │
                             └──── 0..1 Accesorio >── 1 Categoria
                                                │
Sucursal 1 ──< Inventario >── 1 Celular ────────┘
```

- Un **Perfil** tiene muchos **Usuarios**.
- Un **Usuario** (vendedor) registra muchas **Ventas**; un **Cliente** tiene muchas **Ventas**.
- Una **Venta** tiene muchas líneas de **DetalleVenta**.
- Cada línea de **DetalleVenta** es **un celular o un accesorio** (nunca los dos, regla `CHECK`).
- **Inventario** guarda el stock y el precio de cada **Celular** en cada **Sucursal**.
- **Celular** y **Accesorio** pertenecen a una **Categoria** (`CAT-01` Celulares, `CAT-02` Accesorios).

## Diccionario de datos

Las claves primarias son códigos de texto como en el Excel (`CEL-001`, `VTA-002`…).
La aplicación genera el siguiente código automáticamente al registrar.

### Categoria
| Columna     | Tipo        | Nulo | Clave |
|-------------|-------------|------|-------|
| idCategoria | VARCHAR(10) | No   | PK    |
| nombre      | VARCHAR(30) | No   |       |

### Perfil
| Columna      | Tipo        | Nulo | Clave |
|--------------|-------------|------|-------|
| idPerfil     | VARCHAR(10) | No   | PK    |
| nombrePerfil | VARCHAR(30) | No   |       |

### Usuario
| Columna       | Tipo        | Nulo | Clave            |
|---------------|-------------|------|------------------|
| idUsuario     | VARCHAR(10) | No   | PK               |
| nombreUsuario | VARCHAR(30) | No   | Único            |
| contrasena    | VARCHAR(30) | No   |                  |
| idPerfil      | VARCHAR(10) | No   | FK → Perfil      |

### Cliente
| Columna   | Tipo        | Nulo | Clave |
|-----------|-------------|------|-------|
| idCliente | VARCHAR(10) | No   | PK    |
| dni       | CHAR(8)     | No   | Único |
| nombre    | VARCHAR(60) | No   |       |
| celular   | CHAR(9)     | No   |       |
| correo    | VARCHAR(60) | No   |       |

### Celular
| Columna        | Tipo        | Nulo | Clave          |
|----------------|-------------|------|----------------|
| idCelular      | VARCHAR(10) | No   | PK             |
| modelo         | VARCHAR(40) | No   |                |
| marca          | VARCHAR(30) | No   |                |
| almacenamiento | INTEGER     | No   | (GB)           |
| ram            | INTEGER     | No   | (GB)           |
| idCategoria    | VARCHAR(10) | No   | FK → Categoria |

### Accesorio
| Columna     | Tipo          | Nulo | Clave          |
|-------------|---------------|------|----------------|
| idAccesorio | VARCHAR(10)   | No   | PK             |
| nombre      | VARCHAR(60)   | No   |                |
| tipo        | VARCHAR(30)   | No   | Protección, Carga, Audio… |
| precio      | DECIMAL(10,2) | No   |                |
| stock       | INTEGER       | No   |                |
| stockMinimo | INTEGER       | No   |                |
| idCategoria | VARCHAR(10)   | No   | FK → Categoria |

### Sucursal
| Columna      | Tipo        | Nulo | Clave |
|--------------|-------------|------|-------|
| idSucursal   | VARCHAR(10) | No   | PK    |
| numeroTienda | INTEGER     | No   |       |
| ciudad       | VARCHAR(40) | No   |       |
| tipo         | VARCHAR(20) | No   | Flagship, Mall, Quiosco |

### Inventario
| Columna      | Tipo          | Nulo | Clave         |
|--------------|---------------|------|---------------|
| idInventario | VARCHAR(10)   | No   | PK            |
| idCelular    | VARCHAR(10)   | No   | FK → Celular  |
| idSucursal   | VARCHAR(10)   | No   | FK → Sucursal |
| fechaIngreso | DATE          | No   |               |
| stock        | INTEGER       | No   |               |
| precio       | DECIMAL(10,2) | No   |               |

### Venta
| Columna    | Tipo          | Nulo | Clave        |
|------------|---------------|------|--------------|
| idVenta    | VARCHAR(10)   | No   | PK           |
| idCliente  | VARCHAR(10)   | No   | FK → Cliente |
| fechaVenta | DATE          | No   |              |
| pNeto      | DECIMAL(10,2) | No   | total sin IGV |
| igv        | DECIMAL(10,2) | No   | 18 %         |
| pTotal     | DECIMAL(10,2) | No   | total a pagar |
| idUsuario  | VARCHAR(10)   | No   | FK → Usuario |

### DetalleVenta
| Columna        | Tipo          | Nulo | Clave          |
|----------------|---------------|------|----------------|
| idDetalle      | VARCHAR(10)   | No   | PK             |
| idVenta        | VARCHAR(10)   | No   | FK → Venta     |
| idCelular      | VARCHAR(10)   | Sí   | FK → Celular   |
| idAccesorio    | VARCHAR(10)   | Sí   | FK → Accesorio |
| cantidad       | INTEGER       | No   |                |
| precioUnitario | DECIMAL(10,2) | No   |                |
| subtotal       | DECIMAL(10,2) | No   | cantidad × precio |

## Reglas de negocio

1. Los precios de venta **incluyen IGV**. Al registrar una venta:
   `pTotal = Σ subtotal`, `pNeto = pTotal / 1.18`, `igv = pTotal − pNeto`
   (ej. VTA-001: 1424.00 → neto 1206.78, IGV 217.22).
2. Vender un **celular** descuenta stock de su fila de **Inventario** (sucursal);
   vender un **accesorio** descuenta el `stock` de **Accesorio**.
   Si no alcanza el stock, la venta se cancela completa (transacción con *rollback*).
3. Un accesorio con `stock <= stockMinimo` aparece como **stock bajo** en la pantalla de inicio.
4. No se puede eliminar un registro que esté siendo usado por otro (por ejemplo, un
   celular que ya se vendió): la base de datos lo impide por la clave foránea.
