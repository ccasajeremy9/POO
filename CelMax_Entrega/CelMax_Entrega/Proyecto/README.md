# CelMax — Sistema de ventas de celulares y accesorios

Proyecto académico de **Programación Orientada a Objetos**. Aplicación de escritorio
hecha en **Java 21 + JavaFX 21 + Maven**, siguiendo la misma estructura y las mismas
reglas del proyecto **SysVentas** trabajado en clase
(repositorio `POO2026-2-G3`, rama `dev-Sesion11`).

## Contenido del repositorio

```
.
├── README.md                 ← este archivo
├── db/                       ← modelo y documentación de la base de datos
│   ├── trabajo_celmax.architect   (modelo hecho en SQL Power Architect)
│   ├── celmax.sql                 (script completo: tablas + datos de prueba)
│   └── BASE_DATOS.md              (diccionario de datos y relaciones)
└── CelMax/                   ← proyecto Maven (abrir esta carpeta en IntelliJ)
    ├── pom.xml
    ├── docs/MANUAL_CELMAX.md      (arquitectura, dependencias y uso)
    ├── language/                  (textos del menú en español / inglés)
    └── src/main/...
```

## Ejecución rápida

Requisitos: JDK 21 y Maven (o el wrapper `mvnw` incluido).

```bash
cd CelMax
mvn javafx:run
```

La base de datos **H2** se crea sola en `CelMax/data/celmax.mv.db` la primera vez
que se ejecuta, con los datos de ejemplo del Excel del trabajo.

> **Ojo con VS Code:** no uses `mvn clean javafx:run` con VS Code abierto. La extensión de
> Java recompila `target/` al mismo tiempo y aparece el error `Location is not set`.
> Usa solo `mvn javafx:run`.

## Usuarios de prueba

| Usuario | Contraseña   | Perfil        |
|---------|--------------|---------------|
| Isaac   | `Is@c5G#01`  | Administrador |
| Jeremy  | `Jer*Pro15x` | Gerente       |
| Bernabe | `Bern!Ultra7`| Vendedor      |
| Jhoner  | `Jh0n_Fold26`| Vendedor      |

El usuario distingue mayúsculas (`Isaac`, no `isaac`).

Más detalle en [CelMax/docs/MANUAL_CELMAX.md](CelMax/docs/MANUAL_CELMAX.md),
[db/BASE_DATOS.md](db/BASE_DATOS.md) y en la carpeta **Guia_de_Estudio** (plan de estudio,
diagramas, recorrido del código, conceptos de POO, errores comunes y ejercicios).
