# 3. Arquitectura — capas, paquetes y patrones

## 3.1 Arquitectura en capas

Cada capa **solo conoce a la capa de abajo**. Así, si mañana cambiamos H2 por PostgreSQL,
solo se toca la capa de datos.

```mermaid
flowchart TB
    V["VISTA<br/>FXML + CSS<br/>(lo que ve el usuario)"]
    C["CONTROLADOR<br/>controller/*Controller<br/>(eventos de la pantalla)"]
    S["SERVICIO<br/>service/I*Service + impl/*ServiceImp<br/>(reglas de negocio)"]
    R["REPOSITORIO<br/>repository/*Repository<br/>(SQL con JDBC)"]
    D[("BASE DE DATOS H2<br/>data/celmax.mv.db")]
    M["MODELO<br/>model/*<br/>(una clase por tabla)"]

    V -- "clic / onAction" --> C
    C --> S
    S --> R
    R -- "PreparedStatement" --> D
    M -.viaja entre capas.- C
    M -.- S
    M -.- R
```

Versión en texto (por si el diagrama no se dibuja):

```
 Pantalla (FXML) ──► Controller ──► Service ──► Repository ──► H2
        ▲                                                       │
        └──────────── objetos del Model (Cliente, Venta…) ◄─────┘
```

| Capa | Responsabilidad | Ejemplo |
|------|-----------------|---------|
| **Vista** | Mostrar datos y recibir clics | `main_cliente.fxml` |
| **Controlador** | Leer los campos, validar, llamar al servicio, refrescar la tabla | `ClienteController` |
| **Servicio** | Reglas del negocio (IGV, stock, “no existe el ID”) | `VentaServiceImp.calcularTotales()` |
| **Repositorio** | Traducir objetos ⇄ filas SQL | `ClienteRepository.insert()` |
| **Modelo** | Representar una tabla como clase Java | `Cliente` |

## 3.2 Paquetes del proyecto

```
pe.edu.upeu.celmax
├── App / CelMax        arranque (main → Application JavaFX → login)
├── config              AppContext (inyección de dependencias), DatabaseConfig (H2 + Hikari)
├── model               Categoria, Perfil, Usuario, Cliente, Celular, Accesorio,
│                       Sucursal, Inventario, Venta, DetalleVenta
├── repository          ICrudGenericoRepository, AbstractJpaRepository, *Repository
│   └── helper          SqlHelper (JDBC puro)
├── service             I*Service (interfaces), ICrudGenericoService
│   └── impl            *ServiceImp, CrudGenericoServiceImp, MenuMenuItemDaoImp
├── controller          un controlador por pantalla
├── components          TableViewHelper, FormHelper, Toast, ToltipCustom, ColumnInfo, StageManager
├── dto                 ComboBoxOption, ProductoVenta, SessionManager, MenuMenuItenTO
├── enums               TipoProducto (CELULAR / ACCESORIO)
├── exception           ModelNotFoundException
└── utils               UtilsX (lee los archivos de idioma)
```

Recursos (`src/main/resources`): `view/*.fxml`, `css/*.css`, `img/`, `application.properties`,
`schema_celmax.sql`, `data_celmax.sql`.

## 3.3 Inyección de dependencias manual (`AppContext`)

En lugar de usar Spring, el proyecto arma todos los objetos **una sola vez** en `AppContext`
y los guarda en un `Map<Class, Object>`.

```mermaid
flowchart LR
    subgraph AppContext
      direction TB
      R1[ClienteRepository] --> S1[ClienteServiceImp]
      S1 --> C1[ClienteController]
    end
    F[FXMLLoader] -- "setControllerFactory(appContext::getBean)" --> AppContext
```

1. `registrarRepositorios()` crea los repositorios.
2. `registrarServicios()` crea cada servicio **pasándole su repositorio por el constructor**.
3. `registrarControladores()` crea cada controlador **pasándole sus servicios**.
4. Cuando JavaFX carga un `.fxml`, el `FXMLLoader` pide el controlador a `AppContext.getBean()`.

Por eso los controladores tienen `@RequiredArgsConstructor` (Lombok crea el constructor con los
atributos `final`).

## 3.4 Jerarquía genérica (lo más importante para entender el código)

```mermaid
classDiagram
    class ICrudGenericoRepository~T,ID~ {
      <<interface>>
      +save(T) T
      +update(T) T
      +findById(ID) Optional~T~
      +findAll() List~T~
      +deleteById(ID)
      +existsById(ID) boolean
    }
    class SqlHelper~T~ {
      <<abstract>>
      #executeQuery(sql, params) List~T~
      #executeUpdate(conn, sql, params)
      #nextCode(conn, tabla, pk, prefijo, digitos) String
      #mapRow(ResultSet) T*
    }
    class AbstractJpaRepository~T,ID~ {
      <<abstract>>
      +save(T) T
      +findAll() List~T~
      #getTableName() String*
      #getPkColumn() String*
      #insert(conn, T) T*
      #updateRow(conn, T) T*
    }
    class ClienteRepository
    class VentaRepository

    SqlHelper <|-- AbstractJpaRepository
    ICrudGenericoRepository <|.. AbstractJpaRepository
    AbstractJpaRepository <|-- ClienteRepository
    AbstractJpaRepository <|-- VentaRepository
```

- `SqlHelper` sabe **ejecutar SQL** (abre conexión, pone parámetros `?`, recorre el `ResultSet`).
- `AbstractJpaRepository` implementa el CRUD **una sola vez** para todas las tablas.
- Cada repositorio concreto solo dice: nombre de la tabla, nombre de la PK, cómo insertar,
  cómo actualizar y cómo convertir una fila en objeto (`mapRow`).

Lo mismo pasa en servicios:

```mermaid
classDiagram
    class ICrudGenericoService~T,ID~ {
      <<interface>>
      +save(T) T
      +update(ID, T) T
      +findAll() List~T~
      +findById(ID) T
      +delete(ID)
    }
    class CrudGenericoServiceImp~T,ID~ {
      <<abstract>>
      #getRepo() ICrudGenericoRepository*
    }
    class IClienteService {
      <<interface>>
      +listarCombobox() List
    }
    class ClienteServiceImp
    ICrudGenericoService <|.. CrudGenericoServiceImp
    ICrudGenericoService <|-- IClienteService
    CrudGenericoServiceImp <|-- ClienteServiceImp
    IClienteService <|.. ClienteServiceImp
```

## 3.5 Patrones de diseño usados

| Patrón | Dónde | Idea |
|--------|-------|------|
| **Singleton** | `AppContext`, `SessionManager`, `DatabaseConfig` | Una sola instancia para toda la app |
| **Template Method** | `AbstractJpaRepository.save()` llama a `insert()` | El algoritmo general está en la clase padre; los pasos los define el hijo |
| **Repository** | `*Repository` | Esconde el SQL detrás de métodos como `findAll()` |
| **Inyección de dependencias** | `AppContext` + constructores | Los objetos reciben lo que necesitan, no lo crean |
| **MVC** | FXML (vista) + Controller + Model | Separar pantalla, lógica y datos |
| **Builder** | `Cliente.builder().dni(..).build()` (Lombok) | Crear objetos legibles con muchos atributos |
| **DTO** | `ProductoVenta`, `ComboBoxOption` | Objetos solo para transportar datos a la pantalla |

## 3.6 `module-info.java` en una línea cada uno

- `requires javafx.controls / javafx.fxml` → usamos JavaFX.
- `requires java.sql, com.zaxxer.hikari, com.h2database` → base de datos.
- `requires jakarta.validation` → validaciones; `requires static lombok` → solo al compilar.
- `opens ...controller to javafx.fxml` → el FXMLLoader puede inyectar los `@FXML`.
- `opens ...model` y `opens ...dto` → las tablas (`TableView`) pueden leer los getters por reflexión.
