# 8. Preguntas de repaso y ejercicios

Intenta responder **antes** de abrir la respuesta.

## 8.1 Preguntas de repaso

<details><summary><b>1.</b> ¿Cuáles son las capas del proyecto y qué hace cada una?</summary>

Vista (FXML/CSS: lo que se ve), Controlador (eventos de pantalla), Servicio (reglas de negocio),
Repositorio (SQL con JDBC) y Modelo (clases que representan las tablas).
</details>

<details><summary><b>2.</b> ¿Qué hace <code>AppContext</code> y por qué no usamos <code>new</code> en los controladores?</summary>

Es el contenedor de inyección de dependencias: crea una sola vez repositorios, servicios y
controladores, y le pasa a cada uno lo que necesita por el constructor. El `FXMLLoader` le pide
los controladores con `setControllerFactory(appContext::getBean)`.
</details>

<details><summary><b>3.</b> ¿Por qué <code>ClienteRepository</code> no tiene un método <code>findAll()</code> propio y aun así funciona?</summary>

Porque lo hereda de `AbstractJpaRepository`, que arma `SELECT * FROM` + `getTableName()`.
</details>

<details><summary><b>4.</b> ¿Para qué sirve <code>mapRow()</code>?</summary>

Convierte una fila del `ResultSet` en un objeto Java (por ejemplo, una fila de `Cliente` en un
objeto `Cliente`).
</details>

<details><summary><b>5.</b> ¿Cómo se genera el código <code>CLI-006</code>?</summary>

`SqlHelper.nextCode()` busca el mayor número de los códigos que empiezan con `CLI-`, le suma 1
y lo rellena con ceros (`%03d`).
</details>

<details><summary><b>6.</b> ¿Por qué el celular no tiene precio en su tabla?</summary>

Porque el precio y el stock dependen de la sucursal; se guardan en `Inventario`
(Celular + Sucursal + stock + precio).
</details>

<details><summary><b>7.</b> Una venta de S/ 1424.00, ¿cuánto es el neto y el IGV?</summary>

Neto = 1424 / 1.18 = **1206.78**; IGV = 1424 − 1206.78 = **217.22**.
</details>

<details><summary><b>8.</b> ¿Qué pasa si en una venta de 3 productos, el tercero no tiene stock?</summary>

El `UPDATE ... AND stock >= ?` no modifica filas, se lanza una excepción y se hace `rollback`:
no se guarda ni la venta, ni los detalles, ni se descuenta el stock de los otros dos.
</details>

<details><summary><b>9.</b> ¿Dónde se decide qué menús ve un Vendedor?</summary>

En `MenuMenuItemDaoImp.listaAccesos()`, con un `switch` por nombre de perfil.
</details>

<details><summary><b>10.</b> ¿Cómo se conecta un botón del FXML con un método Java?</summary>

Con `onAction="#nombreMetodo"` en el FXML y un método con ese nombre (anotado `@FXML` o público)
en el controlador indicado en `fx:controller`. Los campos se conectan con `fx:id` + `@FXML`.
</details>

<details><summary><b>11.</b> ¿Dónde están las reglas de validación de un cliente?</summary>

En el modelo `Cliente` con anotaciones: `@Pattern(regexp="\\d{8}")` para el DNI,
`@Email` para el correo, etc. `FormHelper.validar()` las ejecuta.
</details>

<details><summary><b>12.</b> ¿Qué es un DTO y cuál usamos?</summary>

Un objeto solo para transportar datos. `ProductoVenta` junta lo que necesita el combo de la venta
(tipo, descripción, precio, stock, idInventario); `ComboBoxOption` guarda clave y texto de un combo.
</details>

<details><summary><b>13.</b> ¿Por qué los datos de prueba no se vuelven a cargar cada vez que se abre la app?</summary>

`DatabaseConfig` solo ejecuta `data_celmax.sql` si la tabla `Perfil` está vacía (base nueva).
Así no se pierden las ventas ni el stock.
</details>

<details><summary><b>14.</b> Diferencia entre <code>save()</code> y <code>update()</code> en el controlador.</summary>

Si `id...CE` es `null` el registro es nuevo → `save()` (INSERT). Si tiene valor, se está editando
→ `update(id, objeto)` (UPDATE).
</details>

## 8.2 Ejercicios prácticos (de menor a mayor dificultad)

1. **Color:** cambia el color principal `-c-primary` en `style.css` y ejecuta.
2. **Mensaje:** cambia el mensaje de error del DNI en `Cliente.java`.
3. **Columna:** agrega la columna “Categoría” a la tabla de accesorios
   (`AccesorioController.initialize()`, campo `idCategoria.nombre`).
4. **Permiso:** haz que el Gerente también pueda ver *Usuarios* (`MenuMenuItemDaoImp`).
5. **Validación:** en `Celular`, haz que la RAM no pueda ser mayor a 32 (`@Max(32)`).
6. **Datos:** agrega un celular nuevo en `data_celmax.sql`, borra `data/` y ejecuta.
7. **Filtro:** agrega un buscador a la pantalla de Sucursales (copia lo de Clientes).
8. **Nueva tabla (reto):** crea la tabla `Marca` y conviértela en combo dentro de Celulares.
   Sigue la receta del manual (`Proyecto/CelMax/docs/MANUAL_CELMAX.md`, sección 8):
   modelo → repositorio → servicio → controlador → FXML → `AppContext` → menú → SQL.
9. **Reporte (reto):** en Inicio, muestra el vendedor con más ventas.

## 8.3 Autoevaluación

Marca lo que ya puedes explicar sin mirar:

- [ ] Dibujar el diagrama de capas.
- [ ] Explicar el recorrido del botón **Guardar**.
- [ ] Explicar la transacción de la venta.
- [ ] Explicar herencia, interfaz y polimorfismo con un ejemplo del proyecto.
- [ ] Explicar la relación Celular – Inventario – Sucursal.
- [ ] Ejecutar el proyecto desde cero en otra computadora.
