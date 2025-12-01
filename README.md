# Pruebas Automatizadas - Altoro Mutual

Suite E2E en Java 21 + Cucumber que asegura los flujos críticos del portal bancario de demostración Altoro Mutual ([demo.testfire.net](https://demo.testfire.net)). Este documento describe **qué prueba la suite, cómo está organizada, el rol de cada archivo clave y cómo ejecutarla o extenderla**.

## Tabla de contenido

- [Pruebas Automatizadas - Altoro Mutual](#pruebas-automatizadas---altoro-mutual)
  - [Sistema bajo prueba](#sistema-bajo-prueba)
  - [Resumen técnico rápido](#resumen-técnico-rápido)
  - [Arquitectura y flujo de ejecución](#arquitectura-y-flujo-de-ejecución)
  - [Archivos esenciales y responsabilidades](#archivos-esenciales-y-responsabilidades)
  - [Estructura del proyecto](#estructura-del-proyecto)
  - [Datos de prueba y formato de Features](#datos-de-prueba-y-formato-de-features)
  - [Dependencias y por qué se usan](#dependencias-y-por-qué-se-usan)
  - [Configuración y parametrización](#configuración-y-parametrización)
  - [Cómo ejecutar la suite](#cómo-ejecutar-la-suite)
  - [Reportes y artefactos](#reportes-y-artefactos)
  - [Cobertura funcional](#cobertura-funcional)
  - [Buenas prácticas y siguientes pasos](#buenas-prácticas-y-siguientes-pasos)

## Sistema bajo prueba

- **URL base:** `https://demo.testfire.net` (sitio demo de Altoro Mutual).
- **Dominio:** banca en línea: login, navegación, cuentas, transferencias, solicitudes y feedback.
- **Objetivo:** validar que los flujos prioritarios siguen funcionando tras cambios de UI, datos o infraestructura.

## Resumen técnico rápido

| Elemento | Valor |
|---------|-------|
| Lenguaje / Build | Java 21 + Maven 3.9+ |
| Framework BDD | Cucumber JVM 7.14.0 (JUnit Platform Engine) |
| UI Driver | Selenium WebDriver 4.14.1 + Chrome administrado por WebDriverManager |
| Inyección de dependencias | PicoContainer vía constructores de steps (`TestContext`) |
| Gestión de datos | Excel único (`src/test/resources/testData/data.xlsx`) leído con Apache POI |
| Reportes | Cucumber HTML/JSON + tablero Masterthought |
| Patrón de diseño | Page Object Model + Hooks per-scenario |

## Arquitectura y flujo de ejecución

1. **Runner (`RunCucumberTest`)** invoca Cucumber sobre JUnit Platform leyendo los feature files definidos en `cucumber.properties`.
2. **Hooks** (`@Before`/`@After` en `Hooks.java`) piden al `TestContext` crear un navegador Chrome mediante `DriverFactory`, limpian cookies y definen timeouts. Al finalizar cada escenario capturan screenshots si hubo fallo y cierran el driver.
3. **Inyección PicoContainer** entrega el mismo `TestContext` al constructor de cada Step Definition. Desde ahí se obtienen Page Objects (`context.getPage(...)`) y datos (`ScenarioDataRepository`).
4. **Page Objects** heredan de `BasePage`, que agrupa utilidades de espera explícita, navegación resiliente (`navigateWithRetry`) y acciones seguras.
5. **ScenarioDataRepository** lee el Excel una sola vez por consulta, ubica la hoja adecuada y devuelve un `Map<String,String>` para poblar cada Scenario Outline.
6. **Assertions** en los steps verifican mensajes, estados de UI y alertas. Los resultados se reportan en JSON y luego en HTML.

El diseño evita estados estáticos, mantiene un navegador por escenario y permite configurar rutas de datos o ambientes únicamente modificando propiedades.

## Archivos esenciales y responsabilidades

| Archivo | Ubicación | Responsabilidad principal |
|---------|-----------|---------------------------|
| `Hooks.java` | `src/test/java/org/example/hooks` | Ejecuta `@Before`/`@After`. Inicializa/quita el WebDriver, toma capturas y adjunta evidencia al escenario cuando hay fallos. |
| `DriverFactory.java` | `src/test/java/org/example/utils` | Crea instancias endurecidas de Chrome (modo incógnito, sin notificaciones, maximizado) y gestiona su ciclo de vida (`createDriver`, `getDriver`, `quitDriver`). |
| `TestContext.java` | `src/test/java/org/example/utils` | Mini contenedor por escenario: expone el driver, cachea Page Objects y garantiza que cada clase de página se construye una sola vez con el WebDriver actual. |
| `BasePage.java` | `src/test/java/org/example/pages` | Clase base para todas las páginas. Centraliza WebDriverWait, navegación, interacción con elementos y logs de acciones. |
| `ScenarioDataRepository.java` | `src/test/java/org/example/utils` | Único punto de acceso al Excel de datos. Permite overrides vía `scenario.excel.path`, y hojas específicas para transferencias (`transfer.excel.sheet`). Devuelve los registros referenciados por `dataId`. |
| `ConfigLoader.java` | `src/test/java/org/example/utils` | Capa de configuración. Lee primero propiedades del sistema/variables de entorno y luego `config/test.properties`, aplicando fallback seguro. |
| `RunCucumberTest.java` | `src/test/java/org/example/runners` | Suite runner. Configura glue, tags y plugins definidos en `cucumber.properties` para ejecutarse bajo JUnit Platform. |
| `*.feature` | `src/test/resources/features` | Especifican los escenarios en Gherkin. Cada Scenario Outline usa `dataId` para buscar datos en el Excel. |

## Estructura del proyecto

```text
Prueba 2/
├─ pom.xml                      # Dependencias y plugins Maven
├─ README.md                    # Este documento
├─ scripts/                     # Utilidades (p.e. migración de datos a Excel)
├─ src/test/
│  ├─ java/
│  │  ├─ org/example/hooks/     # Hooks de Cucumber
│  │  ├─ org/example/pages/     # Page Objects (LoginPage, TransferPage, etc.)
│  │  ├─ org/example/runners/   # Entry point JUnit/Cucumber
│  │  ├─ org/example/steps/     # Step Definitions por feature
│  │  └─ org/example/utils/     # DriverFactory, TestContext, ConfigLoader, ScenarioDataRepository
│  └─ resources/
│     ├─ config/test.properties # Propiedades del entorno bajo prueba
│     ├─ cucumber.properties    # Plugins, glue y ruta de features
│     ├─ junit-platform.properties # Forzar ejecución secuencial
│     ├─ features/*.feature     # Escenarios BDD
│     └─ testData/data.xlsx     # Fuente única de datos parametrizados
└─ target/                      # Resultados de compilación, JSON/HTML de Cucumber
```

## Datos de prueba y formato de Features

### Excel como single source of truth

- **Ruta por defecto:** `src/test/resources/testData/data.xlsx`.
- **Hojas disponibles:** `LoginDatos`, `ConsultaSaldo`, `Feedback`, `SolicitudTarjeta`, `DatosUsuarios` (transferencias) y cualquier otra hoja adicional que se necesite.
- **Campos obligatorios:** todas las hojas comienzan con la columna `dataId`. El repositorio convierte los encabezados a minúsculas, por lo que `Monto`, `monto` o `MONTO` son equivalentes.
- **Overrides:**
  - `-Dscenario.excel.path=/ruta/a/otro.xlsx` usa un workbook distinto para todos los flujos.
  - `-Dtransfer.excel.path` y `-Dtransfer.excel.sheet` permiten probar transferencias con datos aislados sin tocar el archivo principal.

### Formato de Features

- Se utiliza **Scenario Outline + Examples** para todo flujo parametrizable. Cada fila contiene un `dataId` que corresponde a una fila del Excel.
- Ejemplo simplificado:

```gherkin
Scenario Outline: Transferencia con dataset
  Given el usuario autenticado accede a transferencias con los datos "<data_id>"
  When realiza la transferencia configurada
  Then el mensaje de transferencia coincide con los datos configurados

  Examples:
    | data_id                         |
    | transferencia_exitosa_principal |
```

- Los steps cargan los datos mediante `ScenarioDataRepository.getTransferenciaData("transferencia_exitosa_principal")` y reutilizan claves consistentes (`usuario`, `password`, `monto`, `mensajeesperado`, etc.).

## Dependencias y por qué se usan

| Componente | Versión | Motivo |
|-----------|---------|--------|
| Java | 21 | Lenguaje estándar para Selenium y Cucumber. |
| Maven | 3.9+ | Gestiona dependencias, empaquetado y perfiles de ejecución. |
| Selenium WebDriver | 4.14.1 | API para interactuar con Chrome y manipular la UI. |
| WebDriverManager | 5.5.3 | Descarga/gestiona el driver correcto según la versión de Chrome instalada. |
| Cucumber JVM | 7.14.0 | Motor BDD; incluye `cucumber-java`, `cucumber-picocontainer` y el runner para JUnit Platform. |
| JUnit Platform | 1.10.0 | Orquestador; permite ejecutar Cucumber como parte del ecosistema JUnit 5. |
| SLF4J + Logback | 2.0.9 / 1.4.11 | Logging consistente en steps y páginas. |
| Apache POI | 5.2.3 | Lectura/escritura de Excel (`data.xlsx`). |
| Masterthought Cucumber Reporting | 5.7.4 | Genera dashboards HTML ricos a partir del JSON de Cucumber. |

## Configuración y parametrización

### `config/test.properties`

| Clave | Descripción |
|-------|-------------|
| `app.baseUrl` | URL base del ambiente a probar. |
| `app.username` / `app.password` | Credenciales default para login y flujos autenticados. |
| `timeout.seconds` | Timeout estándar para esperas explícitas. |
| `retry.navigation.max` (si existe) | Cantidad de reintentos que `BasePage` aplicará al navegar. |

`ConfigLoader` busca primero variables de entorno o flags `-D`, luego lee este archivo. Ejemplo: `mvn test -Dapp.baseUrl=https://qa.testfire.net`.

### Otros archivos de configuración

- `cucumber.properties`: define glue (`org.example`), ubicación de features, formato de reportes (`pretty`, `json`, `html`) y desactiva `cucumber.publish`.
- `junit-platform.properties`: fija `cucumber.execution.parallel.enabled=false` para asegurar un solo WebDriver a la vez. Cambiarlo a `true` requiere adaptar `DriverFactory` a `ThreadLocal`.

## Cómo ejecutar la suite

### Requisitos previos

1. Java 21 instalado (`java -version`).
2. Maven 3.9 o superior (`mvn -v`).
3. Google Chrome actualizado (WebDriverManager se encarga del driver).
4. Acceso a Internet para descargar dependencias y acceder al sitio demo.

### Comandos típicos

| Acción | Comando |
|--------|---------|
| Ejecutar toda la suite | `mvn test` |
| Ejecutar por etiqueta | `mvn test -Dcucumber.filter.tags="@Transferencia"` |
| Generar dashboard Masterthought | `mvn verify` (ejecuta pruebas y luego `maven-cucumber-reporting`) |
| Cambiar Excel de datos | `mvn test -Dscenario.excel.path=/tmp/mis-datos.xlsx` |

### Flujo al ejecutar `mvn test`

1. Maven descarga dependencias y compila el código en `target/classes`.
2. Cucumber localiza los features y steps configurados.
3. Por cada escenario:
   - `Hooks` arranca Chrome y setea el contexto.
   - Los steps leen datos del Excel y actúan sobre los Page Objects.
   - Se realizan aserciones con JUnit.
   - `Hooks` limpia estado, captura screenshot si falló y cierra el driver.
4. Se generan `target/cucumber.json`, `target/cucumber-reports/report.html` y JUnit XML.

## Reportes y artefactos

- `target/cucumber.json`: salida canónica de Cucumber (input del plugin Masterthought o pipelines CI).
- `target/cucumber-reports/report.html`: reporte HTML ligero generado por Cucumber.
- `target/cucumber-report-html/cucumber-html-reports/index.html`: dashboard Masterthought con métricas, tendencias y gráficos.
- `target/cucumber-results.xml`: reporte JUnit XML para CI.
- **Screenshots:** almacenados como adjuntos en los escenarios fallidos; accesibles desde el reporte HTML.

## Cobertura funcional

- `busqueda_navegacion.feature`: búsqueda de términos y navegación a secciones "Personal" y "Small Business".
- `login.feature`: combinaciones exitosas (`jsmith`, `admin`) y escenarios negativos (campos vacíos, password incorrecta).
- `consulta_saldo.feature`: selección de cuentas (`consulta_cuenta_ahorro`, `consulta_cuenta_inversion`) y verificación del historial/balance.
- `transferencia.feature`: transferencias exitosas y validaciones de alertas (mismas cuentas, montos inválidos) parametrizadas desde la hoja `DatosUsuarios`.
- `solicitud_tarjeta.feature`: flujos de solicitud aprobada/rechazada según la contraseña de confirmación.
- `feedback.feature`: envío de comentarios válidos y validación de errores en correos.

## Buenas prácticas y siguientes pasos

1. **Nuevas páginas:** cree una clase en `org.example.pages`, reciba `WebDriver` en el constructor, extienda de `BasePage` y exponga métodos semánticos. Se obtiene vía `context.getPage(MiNuevaPage.class)`.
2. **Nuevos datos:** agregue filas al Excel con un `dataId` único y use ese identificador en la tabla de Examples. Mantenga encabezados en castellano consistente.
3. **Tags:** utilice etiquetas (`@Login`, `@Smoke`, `@TransferenciaNegativa`) para dividir pipelines o suites rápidas.
4. **Esperas:** reemplace cualquier `Thread.sleep` por métodos de `BasePage` (`waitForVisibility`, `clickWithRetry`) para estabilidad.
5. **CI/CD:** publique los artefactos `target/cucumber-reports`, `target/cucumber-report-html` y `target/cucumber.json`. Configure el job para cargar screenshots como attachments.
6. **Escalabilidad:** si se requiere paralelismo, active `cucumber.execution.parallel.enabled=true` y convierta `DriverFactory` a `ThreadLocal`. También evalúe usar `scenario.excel.path` distintos por pipeline para aislar datos.

Con esta guía un nuevo integrante puede comprender rápidamente la responsabilidad de cada componente, cómo fluye la información desde los features hasta los Page Objects y cómo ejecutar o extender la suite.
