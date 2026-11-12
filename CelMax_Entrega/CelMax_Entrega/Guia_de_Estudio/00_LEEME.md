# Guía de estudio — Proyecto CelMax

Esta carpeta explica **cómo funciona CelMax** y **cómo estudiarlo**, para que todo el grupo
pueda entender el código, ejecutarlo y defenderlo en la exposición.

> Consejo: abre **`Guia_de_Estudio.html`** en el navegador. Ahí está todo junto, con los
> diagramas dibujados (necesita internet para dibujarlos). Los archivos `.md` tienen el mismo
> contenido y también se ven en VS Code o IntelliJ.

## Contenido del ZIP

```
CelMax_Entrega/
├── Proyecto/                 ← el código (abrir la carpeta Proyecto/CelMax en el IDE)
│   ├── README.md
│   ├── db/                   ← modelo de la BD, script SQL y diccionario de datos
│   └── CelMax/               ← proyecto Maven (pom.xml, src/, docs/)
└── Guia_de_Estudio/          ← esta carpeta
    ├── 00_LEEME.md                       (este archivo)
    ├── 01_Plan_de_estudio.md             qué estudiar y en qué orden
    ├── 02_Vision_general.md              qué hace la aplicación
    ├── 03_Arquitectura.md                capas, paquetes y patrones
    ├── 04_Base_de_datos.md               tablas, relaciones y reglas
    ├── 05_Recorrido_del_codigo.md        qué pasa al hacer login, guardar y vender
    ├── 06_Conceptos_POO.md               POO aplicada con ejemplos del código
    ├── 07_Ejecutar_y_errores_comunes.md  instalación, credenciales y soluciones
    ├── 08_Preguntas_y_ejercicios.md      preguntas de repaso y práctica
    └── Guia_de_Estudio.html              todo lo anterior en una sola página
```

## Inicio rápido (5 minutos)

1. Instalar **JDK 21** y **Maven** (o usar `mvnw.cmd` que viene en el proyecto).
2. Abrir una terminal **dentro de** `Proyecto/CelMax` (donde está el `pom.xml`).
3. Ejecutar `mvn javafx:run`.
4. Entrar con **Isaac** / `Is@c5G#01` (Administrador).

| Usuario | Contraseña | Perfil |
|---------|------------|--------|
| Isaac   | `Is@c5G#01`   | Administrador |
| Jeremy  | `Jer*Pro15x`  | Gerente |
| Bernabe | `Bern!Ultra7` | Vendedor |
| Jhoner  | `Jh0n_Fold26` | Vendedor |

## Orden recomendado de lectura

`02 → 03 → 04 → 05 → 06`, siguiendo el **plan del archivo 01**, y al final `08` para repasar.
Si algo no arranca, ve directo al `07`.
