# 7. Ejecutar el proyecto y errores comunes

## 7.1 Requisitos

| Programa | Versión | Cómo comprobar |
|----------|---------|----------------|
| JDK | **21** | `java -version` |
| Maven | 3.8 o superior (o usar `mvnw.cmd` incluido) | `mvn -v` |
| IDE | IntelliJ IDEA o VS Code con *Extension Pack for Java* | — |

La primera vez Maven descarga las librerías de internet (unos minutos).

## 7.2 Ejecutar

1. Descomprimir el ZIP.
2. Abrir una terminal **dentro de** `Proyecto/CelMax` (la carpeta que tiene `pom.xml`).
3. Ejecutar:
   ```bash
   mvn javafx:run
   ```
   Sin Maven instalado: `mvnw.cmd javafx:run` (Windows) o `./mvnw javafx:run` (Linux/Mac).

**IntelliJ:** *File → Open* → elegir `Proyecto/CelMax` → panel *Maven* (derecha) →
*Plugins → javafx → javafx:run*.

**VS Code:** *File → Open Folder* → `Proyecto/CelMax` → terminal integrada → `mvn javafx:run`.

## 7.3 Credenciales de prueba

| Usuario | Contraseña | Perfil |
|---------|------------|--------|
| Isaac   | `Is@c5G#01`   | Administrador |
| Jeremy  | `Jer*Pro15x`  | Gerente |
| Bernabe | `Bern!Ultra7` | Vendedor |
| Jhoner  | `Jh0n_Fold26` | Vendedor |

El usuario distingue mayúsculas y minúsculas (`Isaac`, no `isaac`).

## 7.4 Errores comunes y solución

| Mensaje | Causa | Solución |
|---------|-------|----------|
| `there is no POM in this directory` | La terminal no está en la carpeta del proyecto | `cd` hasta `Proyecto/CelMax` (donde está `pom.xml`) |
| `Location is not set` al arrancar | Se ejecutó `mvn clean ...` con **VS Code abierto**: la extensión de Java recompila `target/` al mismo tiempo y borra los `.fxml` | Ejecutar **sin `clean`**: `mvn javafx:run`. Si hace falta limpiar, cerrar VS Code antes |
| `invalid target release: 21` / `release version 21 not supported` | Maven usa un JDK antiguo | Instalar JDK 21 y apuntar `JAVA_HOME` a él (`mvn -v` debe decir 21) |
| `Database may be already in use` | Hay otra ventana de CelMax abierta usando el mismo archivo H2 | Cerrar la otra ventana (o terminar el proceso `java`) |
| Las tildes del menú salen raras | Archivo de idioma guardado con otra codificación | Guardar `language/*.properties` en UTF-8 |
| No aparece el menú en inglés / sale vacío | Se ejecutó desde otra carpeta y no encuentra `language/` | Ejecutar desde `Proyecto/CelMax` |
| Quiero volver a los datos originales | — | Cerrar la app y borrar la carpeta `Proyecto/CelMax/data/` |
| `SLF4J: Failed to load class ...StaticLoggerBinder` | Aviso de logs, no es un error | Se puede ignorar |

## 7.5 Ver la base de datos por dentro (opcional)

H2 trae una consola web. Con la app **cerrada**:

```bash
java -cp %USERPROFILE%\.m2\repository\com\h2database\h2\2.4.240\h2-2.4.240.jar org.h2.tools.Console
```

En el navegador: JDBC URL `jdbc:h2:file:<ruta>/Proyecto/CelMax/data/celmax`, usuario `sa`,
sin contraseña. Ahí se puede ejecutar `SELECT * FROM Venta;`.
