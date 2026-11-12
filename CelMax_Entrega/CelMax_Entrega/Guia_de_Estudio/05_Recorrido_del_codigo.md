# 5. Recorrido del código — ¿qué pasa cuando…?

Aquí se sigue el camino de **cuatro acciones** reales, archivo por archivo.
Rutas relativas a `Proyecto/CelMax/src/main/java/pe/edu/upeu/celmax/`.

---

## 5.1 …se abre la aplicación

```mermaid
sequenceDiagram
    participant M as App.main()
    participant FX as CelMax (Application)
    participant CTX as AppContext
    participant L as FXMLLoader
    M->>FX: Application.launch(CelMax.class)
    FX->>CTX: AppContext.getInstance()
    Note over CTX: crea repositorios, servicios<br/>y controladores (una sola vez)
    FX->>L: new FXMLLoader("/view/login.fxml")
    FX->>L: setControllerFactory(ctx::getBean)
    L->>CTX: getBean(LoginController.class)
    L-->>FX: Parent (pantalla de login)
    FX->>FX: Scene + style.css → stage.show()
```

1. `App.main()` solo llama a `Application.launch(CelMax.class)`.
2. `CelMax.start()` obtiene el `AppContext`, carga `login.fxml`, le aplica `style.css` y muestra la ventana.
3. La base de datos **aún no se abre**: se abre en la primera consulta (el login).

---

## 5.2 …el usuario inicia sesión

```mermaid
sequenceDiagram
    actor U as Usuario
    participant LC as LoginController
    participant US as UsuarioServiceImp
    participant UR as UsuarioRepository
    participant DB as H2
    U->>LC: escribe usuario/clave y pulsa Ingresar
    LC->>US: loginUsuario("Isaac", "Is@c5G#01")
    US->>UR: findByUsuarioAndClave(...)
    UR->>DB: SELECT u.*, p.nombrePerfil FROM Usuario u JOIN Perfil p ... WHERE ...
    DB-->>UR: 1 fila
    UR-->>US: Optional de Usuario (mapRow)
    US-->>LC: Optional de Usuario
    alt encontrado
        LC->>LC: SessionManager guarda id, nombre y perfil
        LC->>LC: carga maingui.fxml (ventana principal)
    else no encontrado
        LC->>U: "Usuario o contraseña incorrectos"
    end
```

Después, `MainguiController.initialize()`:

1. Muestra el usuario y su perfil en la cabecera.
2. Llama a `graficarMenus()`, que pide a `MenuMenuItemDaoImp.listaAccesos(perfil, idioma)`
   la lista de opciones permitidas y arma el `MenuBar` por código.
3. Abre la pestaña **Inicio**.

Al hacer clic en un menú, `MenuItemListener.handle()` mira el tipo:
`"T"` → abre el FXML en una **pestaña**; `"C"` → **cierra sesión** y vuelve al login.

---

## 5.3 …se guarda un cliente (CRUD típico)

Todos los CRUD (clientes, celulares, accesorios, sucursales, inventario, usuarios) funcionan **igual**.

```mermaid
sequenceDiagram
    actor U as Usuario
    participant V as main_cliente.fxml
    participant C as ClienteController
    participant FH as FormHelper
    participant S as ClienteServiceImp
    participant R as ClienteRepository
    participant DB as H2
    U->>V: llena el formulario y pulsa Guardar
    V->>C: validarFormulario()  (onAction)
    C->>C: arma Cliente con Cliente.builder()
    C->>FH: validar(cliente)  (Jakarta Validation)
    alt hay errores
        FH-->>C: lista de errores
        C->>FH: mostrarErrores() → borde rojo + mensaje
    else sin errores
        C->>S: save(cliente)  o  update(id, cliente)
        S->>R: save(cliente)
        R->>R: abre conexión, setAutoCommit(false)
        R->>DB: nextCode → "CLI-006"
        R->>DB: INSERT INTO Cliente(...) VALUES(?,?,?,?,?)
        R->>DB: commit
        C->>FH: toastOk("Cliente registrado: CLI-006")
        C->>C: clearForm() y listar()
    end
```

Puntos a explicar en la exposición:

- `@FXML TextField txtDni` se conecta con `fx:id="txtDni"` del FXML.
- `onAction="#validarFormulario"` llama al método del mismo nombre.
- Las reglas (`@Pattern(regexp="\\d{8}")` para el DNI) están **en el modelo**, no en el controlador.
- **Editar:** el botón ✎ de la tabla llama a `editForm()`, que llena el formulario y guarda el
  id en `idClienteCE`; al guardar, si `idClienteCE != null` se hace `update` en vez de `save`.
- **Eliminar:** el botón ✖ pide confirmación; si la BD rechaza el borrado por una FK,
  `FormHelper.mensajeError()` lo traduce a un mensaje entendible.
- Los botones ✎ ✖ y las columnas los crea `TableViewHelper` a partir de un `LinkedHashMap`
  “título → atributo”.

---

## 5.4 …se registra una venta (lo más importante)

### Paso A: armar el carrito (solo en memoria)

```mermaid
flowchart TD
    A[initialize] --> B["cbxCliente ← clienteService.listarCombobox()"]
    A --> C["cbxProducto ← ventaService.listarProductosVenta()<br/>celulares desde Inventario + accesorios"]
    D[Clic en Agregar] --> E{¿producto y cantidad válidos?}
    E -- No --> F[mensaje en rojo]
    E -- Sí --> G{¿cantidad + lo que ya está<br/>en el carrito ≤ stock?}
    G -- No --> H[Stock insuficiente]
    G -- Sí --> I{¿ya está en el carrito?}
    I -- Sí --> J[sumar cantidad y subtotal]
    I -- No --> K[nuevo DetalleVenta]
    J --> L[calcularTotales → neto, IGV, total]
    K --> L
```

- El carrito es una `ObservableList<DetalleVenta>`: la tabla se actualiza sola al agregar o quitar.
- `ProductoVenta` (paquete `dto`) es lo que se muestra en el combo: tipo, descripción,
  precio, stock e `idInventario` (para saber de qué sucursal sale el celular).

### Paso B: confirmar la venta (una transacción)

```mermaid
sequenceDiagram
    participant VC as VentaController
    participant VS as VentaServiceImp
    participant VR as VentaRepository
    participant DB as H2
    VC->>VS: registrarVenta(cliente, vendedor, carrito)
    VS->>VS: calcularTotales() → neto, igv, total
    VS->>VR: save(venta)
    VR->>DB: setAutoCommit(false)  (inicia transacción)
    VR->>DB: INSERT INTO Venta ...  (VTA-006)
    loop por cada línea del carrito
        VR->>DB: INSERT INTO DetalleVenta ...
        VR->>DB: UPDATE Inventario/Accesorio SET stock = stock - ? WHERE ... AND stock >= ?
        alt 0 filas actualizadas (no hay stock)
            VR->>DB: ROLLBACK (se deshace TODO)
            VR-->>VC: excepción "Stock insuficiente..."
        end
    end
    VR->>DB: COMMIT
    VR-->>VC: venta con su código
    VC->>VC: toast "Venta VTA-006 registrada" y limpiar()
```

**¿Por qué una transacción?** Porque la venta son varias operaciones (cabecera, detalles y
stock). O se guardan **todas**, o **ninguna**. Si fallara a la mitad sin transacción, quedaría
una venta sin detalle o un stock descontado sin venta.

El truco está en `AbstractJpaRepository.save()`:

```java
conn.setAutoCommit(false);
try {
    T result = insert(conn, entity);   // VentaRepository.insert hace todo el trabajo
    conn.commit();
    return result;
} catch (Exception e) {
    conn.rollback();                   // si algo falla, se deshace todo
    throw e;
}
```

---

## 5.5 Mapa rápido: “quiero cambiar X, ¿dónde voy?”

| Quiero… | Archivo |
|---------|---------|
| Cambiar colores o tipografía | `resources/css/style.css` (y `estilo-oscuro.css`) |
| Mover un campo de una pantalla | `resources/view/main_*.fxml` |
| Cambiar un mensaje de validación | `model/<Entidad>.java` (anotaciones) |
| Cambiar qué menús ve un perfil | `service/impl/MenuMenuItemDaoImp.java` |
| Cambiar la tasa de IGV | `service/IVentaService.java` → `TASA_IGV` |
| Cambiar un SQL | `repository/<Entidad>Repository.java` |
| Agregar/quitar columnas de una tabla en pantalla | `controller/<Entidad>Controller.initialize()` |
| Cambiar los datos iniciales | `resources/data_celmax.sql` (y borrar la carpeta `data/`) |
| Cambiar los textos del menú | `language/idiomas-es.properties` / `idiomas-en.properties` |
