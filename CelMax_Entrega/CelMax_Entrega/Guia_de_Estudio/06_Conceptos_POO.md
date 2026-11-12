# 6. Conceptos de POO aplicados en CelMax

Cada concepto con **dónde está en el código**, para poder señalarlo en la exposición.

## 6.1 Clase y objeto

Una **clase** es el molde; un **objeto** es una instancia concreta.

```java
public class Cliente {            // clase
    private String idCliente;
    private String dni;
    ...
}
Cliente c = Cliente.builder().dni("45172983").nombre("Rosa").build();  // objeto
```

## 6.2 Encapsulamiento

Los atributos son `private` y se accede con getters/setters. Lombok los genera con `@Data`:

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Accesorio {
    private Integer stock;
    private Integer stockMinimo;
    public boolean isStockBajo() { return stock <= stockMinimo; }   // comportamiento propio
}
```

También en `DatabaseConfig`: el pool `dataSource` es `private static` y solo se entrega con
`getConnection()`.

## 6.3 Herencia

Una clase hija **reutiliza** lo de la clase padre (`extends`).

```
SqlHelper<T>
   └── AbstractJpaRepository<T, ID>
          ├── ClienteRepository
          ├── CelularRepository
          └── VentaRepository ...
```

`ClienteRepository` no escribe `findAll()`, `save()` ni `deleteById()`: los **hereda**.

## 6.4 Abstracción y clases abstractas

`AbstractJpaRepository` es `abstract`: no se puede instanciar y obliga a los hijos a
implementar los métodos abstractos.

```java
protected abstract String getTableName();
protected abstract T insert(Connection conn, T entity) throws SQLException;
protected abstract T mapRow(ResultSet rs) throws SQLException;
```

## 6.5 Interfaces

Una **interfaz** es un contrato: qué se puede hacer, sin decir cómo.

```java
public interface IClienteService extends ICrudGenericoService<Cliente, String> {
    List<ComboBoxOption> listarCombobox();
}
public class ClienteServiceImp extends CrudGenericoServiceImp<Cliente, String>
        implements IClienteService { ... }
```

Los controladores dependen de la **interfaz** (`IClienteService`), no de la implementación.

## 6.6 Polimorfismo

- **Sobrescritura (`@Override`):** `VentaRepository` sobrescribe `findAll()` para hacer un JOIN
  con Cliente y Usuario; el resto de código sigue llamando a `findAll()` sin saber la diferencia.
- **Por interfaz:** `AppContext.getBean(IVentaService.class)` devuelve un `VentaServiceImp`,
  pero se usa como `IVentaService`.
- **Sobrecarga:** `TableViewHelper.addColumnsInOrderWithSize(...)` existe con 2 y con 4 parámetros.

## 6.7 Genéricos

`<T, ID>` permite escribir el CRUD **una sola vez** para cualquier entidad:

```java
public interface ICrudGenericoRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
}
// T = Cliente, ID = String
public class ClienteRepository extends AbstractJpaRepository<Cliente, String> { ... }
```

## 6.8 Composición / asociación

Un objeto **contiene** a otro:

- `Venta` tiene un `Cliente`, un `Usuario` y una `List<DetalleVenta>`.
- `Inventario` tiene un `Celular` y una `Sucursal`.
- `ClienteController` tiene un `IClienteService` (se lo pasan por el constructor).

## 6.9 Enumeraciones

```java
public enum TipoProducto { CELULAR, ACCESORIO }
```
Se usa en `ProductoVenta` para saber si se descuenta stock de `Inventario` o de `Accesorio`.

## 6.10 Excepciones

- `ModelNotFoundException extends RuntimeException`: la lanza el servicio cuando el ID no existe.
- `try / catch` en los controladores: si algo falla se muestra un mensaje en vez de cerrar la app.
- `try-with-resources` en `SqlHelper`: la conexión se cierra sola.

```java
try (Connection conn = openConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) { ... }
```

## 6.11 `static` y Singleton

```java
public static synchronized AppContext getInstance() {
    if (instance == null) instance = new AppContext();
    return instance;
}
```
`static` pertenece a la clase, no al objeto. El constructor es `private` → nadie más puede crear otro.

## 6.12 Lambdas y referencias a métodos

```java
txtFiltroDato.textProperty().addListener((obs, viejo, nuevo) -> filtrar(nuevo));  // lambda
new TableViewHelper<Cliente>().addColumnsInOrderWithSize(tableView, columns,
        this::editForm, this::eliminar);                                         // referencia a método
fxmlLoader.setControllerFactory(appContext::getBean);
```

## 6.13 `Optional`

Evita los `null`: `findById()` devuelve `Optional<T>`.

```java
us.loginUsuario(usuario, clave).ifPresentOrElse(
    u -> abrirPantalla(e, u),
    () -> lblError.setText("Usuario o contraseña incorrectos"));
```

## 6.14 Anotaciones

| Anotación | Quién la usa | Para qué |
|-----------|--------------|----------|
| `@Data`, `@Builder`, `@RequiredArgsConstructor` | Lombok | Generar código repetitivo |
| `@NotBlank`, `@Pattern`, `@Email`, `@Positive` | Jakarta Validation | Validar formularios |
| `@FXML` | JavaFX | Conectar atributos y métodos con el FXML |
| `@Override` | Compilador | Verificar que se sobrescribe un método |
