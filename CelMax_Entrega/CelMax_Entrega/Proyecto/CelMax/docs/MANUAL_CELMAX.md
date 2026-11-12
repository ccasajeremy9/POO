# Manual básico — CelMax (JavaFX + Maven)

Sistema de escritorio para la venta de celulares y accesorios de la empresa **CelMax**.
Se construyó copiando la estructura del proyecto **SysVentas** visto en clase
(`POO2026-2-G3`, rama `dev-Sesion11`): mismas capas, mismos paquetes, mismas clases base
y el mismo tipo de base de datos.

---

## 1. Arquitectura

```
View (FXML + CSS)
   │  onAction="#metodo"
   ▼
Controller (JavaFX)          ← recibe los servicios por constructor
   │
   ▼
Service (interfaz + Imp)     ← reglas de negocio (IGV, stock, códigos)
   │
   ▼
Repository (JDBC puro)       ← SQL contra H2 usando SqlHelper / AbstractJpaRepository
   │
   ▼
DatabaseConfig (HikariCP + H2)
```

- **Inyección de dependencias manual** en `config/AppContext` (patrón *Singleton* + mapa
  `Clase → Objeto`). El `FXMLLoader` pide los controladores con
  `setControllerFactory(appContext::getBean)`.
- **Template Method**: `AbstractJpaRepository` implementa `save/update/findById/findAll/delete`
  y cada repositorio concreto solo define la tabla, la PK, `insert`, `updateRow` y `mapRow`.
- **CRUD genérico** en servicios: `ICrudGenericoService<T,ID>` + `CrudGenericoServiceImp<T,ID>`.
- **Validación** de formularios con anotaciones de Jakarta Validation en los modelos
  (`@NotBlank`, `@Pattern`, `@Email`, `@Positive`…).
- **Menú por perfil** en `MenuMenuItemDaoImp` (igual que SysVentas).

## 2. Estructura del proyecto

```
CelMax/
├── pom.xml
├── language/                       idiomas-es / idiomas-en (textos del menú)
├── data/                           (se crea sola) base H2 celmax.mv.db
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── pe/edu/upeu/celmax/
    │       ├── App.java            main()
    │       ├── CelMax.java         Application JavaFX (abre el login)
    │       ├── components/         TableViewHelper, ColumnInfo, Toast, ToltipCustom, StageManager,
    │       │                       FormHelper (validar / mensajes, común a todos los CRUD)
    │       ├── config/             AppContext (DI), DatabaseConfig (H2 + Hikari)
    │       ├── controller/         Login, Maingui, Inicio, Cliente, Celular, Accesorio,
    │       │                       Sucursal, Inventario, Usuario, Venta, HistorialVenta
    │       ├── dto/                ComboBoxOption, MenuMenuItenTO, SessionManager,
    │       │                       ProductoVenta (opción del combo de venta)
    │       ├── enums/              TipoProducto (CELULAR / ACCESORIO)
    │       ├── exception/          ModelNotFoundException
    │       ├── model/              Una clase por tabla (Categoria, Perfil, Usuario, Cliente,
    │       │                       Celular, Accesorio, Sucursal, Inventario, Venta, DetalleVenta)
    │       ├── repository/         ICrudGenericoRepository, AbstractJpaRepository,
    │       │   └── helper/         SqlHelper   + un repositorio por tabla
    │       ├── service/            I...Service  (interfaces)
    │       │   └── impl/           ...ServiceImp (implementaciones)
    │       └── utils/              UtilsX (lectura de idiomas)
    └── resources/
        ├── application.properties  configuración de la BD
        ├── schema_celmax.sql       tablas (se ejecuta en cada arranque)
        ├── data_celmax.sql         datos de prueba (solo si la BD está vacía)
        ├── css/                    style.css (claro) y estilo-oscuro.css
        ├── img/                    íconos de editar / eliminar
        └── view/                   login, maingui, main_inicio, main_cliente, main_celular,
                                    main_accesorio, main_sucursal, main_inventario,
                                    main_usuario, main_venta, main_historial
```

## 3. Dependencias (`pom.xml`)

Se usan las **mismas versiones** que SysVentas. **No se agregó ninguna dependencia nueva.**

| Dependencia | Versión | Para qué se usa en CelMax |
|-------------|---------|---------------------------|
| `org.openjfx:javafx-controls` | 21.0.6 | Ventanas, tablas, botones |
| `org.openjfx:javafx-fxml` | 21.0.6 | Cargar las vistas `.fxml` |
| `org.controlsfx:controlsfx` | 11.2.1 | Controles extra de JavaFX (igual que SysVentas) |
| `org.projectlombok:lombok` | 1.18.46 | `@Data`, `@Builder`, `@RequiredArgsConstructor` en modelos y controladores |
| `org.hibernate.validator:hibernate-validator` | 8.0.3.Final | Motor de validación de formularios |
| `jakarta.validation:jakarta.validation-api` | 3.0.2 | Anotaciones `@NotBlank`, `@Pattern`… |
| `org.glassfish.expressly:expressly` | 5.0.0 | Lenguaje de expresiones que necesita Hibernate Validator |
| `com.h2database:h2` | 2.4.240 | Base de datos embebida en archivo |
| `com.zaxxer:HikariCP` | 6.3.0 | Pool de conexiones |
| `ch.qos.logback:logback-classic` | 1.4.14 | Logs (SLF4J) |
| `org.junit.jupiter` (api / engine) | 5.12.1 | Pruebas (scope test) |

**Dependencias de SysVentas que NO se incluyeron** (porque CelMax no usa esas funciones,
así el proyecto queda más simple):

| Dependencia | Motivo |
|-------------|--------|
| `formsfx-core` | No se generan formularios con FormsFX |
| `jsoup`, `gson` | Se usaban para consultar DNI en una API externa |
| `escpos-coffee` | Impresión en ticketera térmica |
| `openpdf` | Generación del comprobante en PDF |

## 4. Base de datos

Configuración en `src/main/resources/application.properties`:

```properties
db.url=jdbc:h2:file:./data/celmax
db.username=sa
db.password=
db.ddl.auto=true
db.ddl.script=schema_celmax.sql
db.data.script=data_celmax.sql
```

`DatabaseConfig` es la misma clase de SysVentas con un único cambio: después de crear
las tablas, ejecuta `data_celmax.sql` **solo si la tabla Perfil está vacía**. En SysVentas
los datos se volvían a cargar en cada arranque con `MERGE`, lo que aquí reiniciaría el stock.

El detalle de tablas y relaciones está en [`../../db/BASE_DATOS.md`](../../db/BASE_DATOS.md).

### Diferencias con SysVentas (para tenerlas claras)

| Tema | SysVentas | CelMax |
|------|-----------|--------|
| Claves primarias | Números autoincrementales | Códigos de texto como en el Excel (`CEL-001`); `SqlHelper.nextCode()` genera el siguiente |
| Carrito de venta | Tabla `vent_carrito` | Lista en memoria (`ObservableList<DetalleVenta>`) |
| Guardar venta | Varios servicios | `VentaRepository.insert()` guarda cabecera + detalle + descuenta stock en **una** transacción |
| Validación en controladores | Código repetido en cada uno | Reunido en `components/FormHelper` |
| `TableViewHelper` | Siempre con columna Acciones | Si se pasa `null` no agrega botones; formatea decimales y fechas |

## 5. Perfiles y menú

| Menú | Opción | Administrador | Gerente | Vendedor |
|------|--------|:---:|:---:|:---:|
| Principal | Inicio / Salir | ✔ | ✔ | ✔ |
| Catálogo | Celulares, Accesorios | ✔ | ✔ | |
| Almacén | Sucursales, Inventario | ✔ | ✔ | |
| Ventas | Clientes, Historial de ventas | ✔ | ✔ | ✔ |
| Ventas | Registrar venta | ✔ | | ✔ |
| Administración | Usuarios | ✔ | | |

El menú también tiene **Cambiar estilo** (claro / oscuro) e **Idioma** (español / inglés),
como en SysVentas.

## 6. Cómo ejecutar

1. Instalar **JDK 21** (y Maven, o usar `mvnw`).
2. Abrir la carpeta `CelMax` en IntelliJ IDEA como proyecto Maven.
3. Ejecutar:
   ```bash
   mvn javafx:run
   ```
   o en Windows sin Maven instalado: `mvnw.cmd javafx:run`.
4. Iniciar sesión con un usuario de prueba, por ejemplo **Isaac / `Is@c5G#01`** (Administrador).

> Importante: ejecutar desde la carpeta `CelMax`, porque la base (`data/`) y los idiomas
> (`language/`) se buscan relativos a esa carpeta.
>
> En **VS Code** no uses `mvn clean javafx:run`: la extensión de Java recompila `target/` al
> mismo tiempo y aparece `Location is not set`. Usa `mvn javafx:run`.
> Más errores y soluciones en `Guia_de_Estudio/07_Ejecutar_y_errores_comunes.md`.

## 7. Flujo de una venta

1. Menú **Ventas → Registrar venta**.
2. Elegir el **cliente** (si no existe, registrarlo antes en *Clientes*).
3. Elegir un **producto**: los celulares aparecen por sucursal con su precio y stock del
   inventario; los accesorios con su precio y stock propio.
4. Escribir la **cantidad** y pulsar **Agregar**. Se repite para cada producto.
5. El sistema calcula **Neto, IGV (18 %) y Total**.
6. Pulsar **Registrar venta**: se guardan `Venta` + `DetalleVenta` y se descuenta el stock
   en una sola transacción.
7. La venta aparece en **Ventas → Historial de ventas**, donde al seleccionarla se ve su detalle.

## 8. Cómo agregar una nueva tabla (receta)

1. `model/` → clase con `@Data @Builder @NoArgsConstructor @AllArgsConstructor` y validaciones.
2. `repository/` → `XRepository extends AbstractJpaRepository<X, String>`.
3. `service/` → `IXService extends ICrudGenericoService<X, String>` y `impl/XServiceImp`.
4. `controller/` → `XController` con `@RequiredArgsConstructor`.
5. `resources/view/main_x.fxml` con `fx:controller` apuntando al controlador.
6. Registrar repositorio, servicio y controlador en `AppContext`.
7. Agregar la opción en `MenuMenuItemDaoImp` y la tabla en `schema_celmax.sql`.
