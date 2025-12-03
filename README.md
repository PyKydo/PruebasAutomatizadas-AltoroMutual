# Pruebas Automatizadas - Altoro Mutual

Suite E2E en Java 21 + Cucumber que asegura los flujos críticos del portal bancario de demostración Altoro Mutual ([demo.testfire.net](https://demo.testfire.net)). Este documento describe **qué prueba la suite, cómo está organizada, el rol de cada archivo clave y cómo ejecutarla o extenderla**.

## Tabla de contenido

- [Pruebas Automatizadas - Altoro Mutual](#pruebas-automatizadas---altoro-mutual)
  - [Sistema bajo prueba](#sistema-bajo-prueba)
  - [Resumen técnico rápido](#resumen-técnico-rápido)
  - [Arquitectura y flujo de ejecución](#arquitectura-y-flujo-de-ejecución)
  - [Archivos esenciales y responsabilidades](#archivos-esenciales-y-responsabilidades)
  - [Estructura del proyecto](#estructura-del-proyecto)
  - [Análisis detallado del código (`src/test/java/org/example`)](#análisis-detallado-del-código-srctestjavaorgexample)
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
| Gestión de Page Objects | Localizador interno `Config.pagina(...)` que instancia páginas vía reflexión y las cachea por hilo (la dependencia a PicoContainer queda disponible para extensiones futuras) |
| Gestión de datos | Excel único (`src/test/resources/testData/data.xlsx`) procesado por `ExcelUtils` (Apache POI) |
| Reportes | Cucumber HTML/JSON + tablero Masterthought |
| Patrón de diseño | Page Object Model + Hooks per-scenario |

## Arquitectura y flujo de ejecución

1. **Runner (`RunCucumberTest`)** expone una suite de JUnit Platform limitada al engine `cucumber`, asigna el `glue` (`org.example.steps, org.example.hooks`) y habilita los plugins de reporte (`pretty`, HTML, JSON y JUnit XML). El runner funciona como único punto de entrada para IDE, terminal y CI.
2. **Ciclo de Hooks (`Hooks.java`)**: el `@Before` ejecuta `Driver.iniciar()` antes de cada escenario, dejando un `WebDriver` por hilo (ThreadLocal). El `@After` toma screenshots si el escenario falla, ejecuta `Driver.cerrar()` y pide a `Config.limpiarCachePaginas()` desechar instancias reutilizadas.
3. **`Driver`** controla toda la configuración del navegador: usa WebDriverManager para alinear el binario de ChromeDriver con la versión instalada, inyecta flags de endurecimiento (incógnito, bloqueo de pop-ups, `--headless` opcional) y expone `driver()` para el resto de las capas.
4. **`Config.pagina(Class)`** actúa como micro contenedor: crea Page Objects vía reflexión entregándoles el `WebDriver` activo y los cachea en un `ThreadLocal<Map<Class<?>,Object>>`. Así, cada step puede pedir la misma página sin volver a instanciarla y sin compartir referencias con otros escenarios.
5. **Step Definitions** son los orquestadores: cargan los datos del Excel mediante `ExcelUtils`, invocan los métodos semánticos de los Page Objects y ejecutan las aserciones JUnit sobre los textos o alertas retornados por las páginas.
6. **`BasePage`** concentra sincronización y navegación resiliente: todas las páginas concretas heredan de ella para usar `WebDriverWait`, interacción segura (`ingresarTexto`, `hacerClick`) y reintentos para `driver.get()` con pausas configurables.
7. **Finalización**: tras cada escenario Cucumber genera los artefactos configurados, y en la fase `verify` Maven ejecuta `maven-cucumber-reporting` para fabricar el dashboard Masterthought a partir de `cucumber.json`.

Con este flujo se garantiza aislamiento por escenario (driver y páginas por ThreadLocal), datos externos centralizados y un punto único de configuración del browser. Cambiar propiedades (`-Dapp.baseUrl`, `-Dbrowser.headless=true`, etc.) es suficiente para apuntar a nuevos ambientes sin tocar código.

## Archivos esenciales y responsabilidades

| Archivo | Ubicación | Responsabilidad principal |
|---------|-----------|---------------------------|
| `RunCucumberTest.java` | `src/test/java/org/example/runners` | Expone la suite JUnit Platform que ejecuta el engine Cucumber con el glue `org.example.*` y los plugins de reporte configurados. |
| `Hooks.java` | `src/test/java/org/example/hooks` | Ejecuta `@Before`/`@After`: levanta y cierra el `WebDriver`, adjunta screenshots a escenarios fallidos y limpia la caché de páginas. |
| `Driver.java` | `src/test/java/org/example/utils` | Administra un `ThreadLocal<WebDriver>`, configura Chrome (flags de endurecimiento/headless) con WebDriverManager y expone `iniciar`, `driver`, `cerrar`. |
| `Config.java` | `src/test/java/org/example/utils` | Resuelve propiedades (System Properties → Variables de entorno → `config/defaults.properties`) y funciona como localizador/refinería de Page Objects (`pagina(Class<?>)`, `limpiarCachePaginas()`). |
| `ExcelUtils.java` | `src/test/java/org/example/utils` | Lee `testData/data.xlsx` (o el Excel indicado en `scenario.excel.path`), interpreta encabezados normalizados y devuelve mapas de datos por hoja temática (`datosLogin`, `datosTransferencia`, etc.). |
| `BasePage.java` | `src/test/java/org/example/pages` | Clase base con `WebDriverWait`, métodos seguros de interacción (`esperarVisibilidad`, `ingresarTexto`, `hacerClick`) y navegación con reintentos parametrizables. |
| `LoginPage.java`, `BusquedaNavegacionPage.java`, `ConsultaSaldoPage.java`, `FeedbackPage.java`, `SolicitudTarjetaPage.java`, `TransferenciaPage.java` | `src/test/java/org/example/pages` | Page Objects concretos que encapsulan selectores y acciones de los flujos críticos (login, búsquedas, saldos, feedback, solicitud de tarjeta y transferencias). |
| `steps/*.java` | `src/test/java/org/example/steps` | Step Definitions en castellano; orquestan cada escenario, llaman a las páginas vía `Config.pagina(...)`, consumen datos de `ExcelUtils` y validan el resultado con JUnit. |
| `features/*.feature` | `src/test/resources/features` | Escenarios BDD (Gherkin) agrupados por dominio (`@Login`, `@Transferencia`, etc.) que definen `Scenario Outline` y `dataId` usados por el Excel. |

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
│  │  └─ org/example/utils/     # Config (propiedades y páginas), Driver, ExcelUtils
│  └─ resources/
│     ├─ config/                # defaults.properties, cucumber.properties, junit-platform.properties
│     ├─ features/*.feature     # Escenarios BDD
│     └─ testData/data.xlsx     # Fuente única de datos parametrizados
└─ target/                      # Resultados de compilación, JSON/HTML de Cucumber
```

## Análisis detallado del código (`src/test/java/org/example`)

### `hooks/`

- **Hooks.java**: registra logs por escenario, inicializa el `WebDriver` a través de `Driver.iniciar()`, adjunta screenshots `image/png` cuando `Scenario.isFailed()` y garantiza que cada escenario termine con `Driver.cerrar()` y `Config.limpiarCachePaginas()`.

### `runners/`

- **RunCucumberTest.java**: suite de anotación pura (`@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features")`). Declara las `ConfigurationParameter` necesarias para definir glue y plugins sin depender de archivos externos.

### `utils/`

- **Config.java**: carga `config/defaults.properties`, aplica la prioridad System Property → Variable de entorno → Defaults y expone `config(...)`/`configInt(...)`. Su método estrella `pagina(Class)` usa reflexión para crear Page Objects con el `WebDriver` activo y los cachea por hilo.
- **Driver.java**: mantiene un `ThreadLocal<WebDriver>`, configura Chrome mediante `ChromeOptions` (incógnito, bloqueo de notificaciones, tamaño fijo en headless), delega en WebDriverManager 6.1.0 para sincronizar el binario y ofrece `iniciar/driver/cerrar` como API pública.
- **ExcelUtils.java**: abre el workbook configurado (classpath o filesystem), normaliza encabezados a minúsculas y expone métodos por dominio (`datosLogin`, `datosTransferencia`, etc.). Cada método devuelve un `Map<String,String>` indexado por `dataId`, garantizando que la estructura de datos se mantenga homogénea en todos los features.

### `pages/`

- **BasePage.java**: constructor recibe `WebDriver`, crea un `WebDriverWait` basado en `timeout.seconds` y habilita utilidades localizadas (`esperarVisibilidad`, `esperarElementoClickable`, `ingresarTexto`, `hacerClick`, `obtenerTexto`, `estaElementoPresente` y `navegarConReintentos`).
- **LoginPage.java**: abre `login.jsp`, limpia alertas residuales, ejecuta login con valores nulos seguros y permite validar si la sesión quedó activa (link "Sign Off") o si existe un mensaje de error DOM.
- **BusquedaNavegacionPage.java**: opera sobre la home y la búsqueda de Altoro. Implementa `buscarTermino`, `irASeccion` con un `Map<String, By>` para secciones soportadas y `obtenerTituloPrincipal` para validar encabezados `h1`.
- **ConsultaSaldoPage.java**: gestiona el combo `listAccounts`, el botón `btnGetAccount` y los elementos que muestran historial/saldo (`<h1>` y la celda "Ending balance").
- **FeedbackPage.java**: navega a `feedback.jsp`, completa el formulario (name/email/subject/comments) y lee el párrafo de respuesta tras enviar.
- **SolicitudTarjetaPage.java**: presume que el usuario ya está autenticado, hace click en `apply.jsp`, captura password, envía la solicitud y retorna el mensaje correspondiente (éxito o error) según qué elemento esté presente.
- **TransferenciaPage.java**: accede al menú `MenuHyperLink3`, sincroniza la pantalla usando `WebDriverWait`, permite elegir cuentas origen/destino via `Select`, definir monto, enviar la transferencia, leer el mensaje DOM o aceptar alertas cuando se espera una validación del lado del navegador.

### `steps/`

- **BusquedaNavegacionStepDefinitions.java**: usa `Config.pagina(BusquedaNavegacionPage.class)` para abrir el portal, buscar términos y asegurar que el encabezado incluya el texto proporcionado.
- **LoginStepDefinitions.java**: referencia `ExcelUtils.datosLogin`, soporta alias de columnas (`password`, `clave`, `contraseña`) y decide si debe verse el link "Sign Off" o un mensaje de error.
- **ConsultaSaldoStepDefinitions.java**: autentica con las credenciales default y luego parametriza la cuenta `listAccounts` según los datos `ConsultaSaldo` del Excel; valida que el título contenga el identificador y que el saldo no esté vacío.
- **FeedbackStepDefinitions.java**: combina `ExcelUtils.datosFeedback` con `FeedbackPage` para enviar formularios válidos o negativos, comparando el mensaje resultante con `mensajeesperado`.
- **SolicitudTarjetaStepDefinitions.java**: reutiliza login default, abre la pantalla de solicitud, toma contraseñas definidas en `SolicitudTarjeta` y compara el resultado contra el catálogo local `RESULT_MESSAGES` (aprobado/rechazado).
- **TransferenciaStepDefinitions.java**: dataset por dataset (`Transferencias`) decide credenciales, cuentas, montos y tipo de verificación (mensaje en página o alerta). Expone métodos privados `fallback`/`required` que simplifican la lectura de columnas del Excel.

### Recursos complementarios (`src/test/resources`)

- **`config/defaults.properties`**: valores de arranque (URL, credenciales, timeouts, banderas de navegación y headless, ruta del Excel). Puede replicarse por ambiente creando archivos alternativos y cargándolos vía `-Dscenario.excel.path` o `-Dapp.baseUrl`.
- **`config/cucumber.properties` y `config/junit-platform.properties`**: switches para el publish de Cucumber y para deshabilitar el paralelismo respectivamente.
- **`features/*.feature`**:
  - `busqueda_navegacion.feature`: pruebas @Busqueda de búsqueda y navegación.
  - `login.feature`: Scenario Outline para credenciales válidas/inválidas.
  - `consulta_saldo.feature`: flujos parametrizados para historial y balance.
  - `feedback.feature`: validaciones positivas/negativas del formulario.
  - `solicitud_tarjeta.feature`: decisiones aprobada/rechazada basadas en password.
  - `transferencia.feature`: transferencias exitosas y validaciones de alertas.
- **`testData/data.xlsx`**: workbook maestro con hojas `LoginDatos`, `ConsultaSaldo`, `Feedback`, `SolicitudTarjeta` y `Transferencias`. La columna `dataId` es obligatoria y es la que se referencia desde los `Examples` de cada feature.

## Datos de prueba y formato de Features

### Excel como single source of truth

- **Ruta por defecto:** `src/test/resources/testData/data.xlsx`.
- **Hojas disponibles:** `LoginDatos`, `ConsultaSaldo`, `Feedback`, `SolicitudTarjeta`, `Transferencias` y cualquier otra hoja adicional que se necesite.
- **Campos obligatorios:** todas las hojas comienzan con la columna `dataId`. El repositorio convierte los encabezados a minúsculas, por lo que `Monto`, `monto` o `MONTO` son equivalentes.
- **Overrides:**
  - `-Dscenario.excel.path=/ruta/a/otro.xlsx` usa un workbook distinto para todos los flujos.

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

- Los steps obtienen los registros con `ExcelUtils.datosTransferencia("transferencia_exitosa_principal")` y reutilizan claves consistentes (`usuario`, `password`, `monto`, `mensajeesperado`, etc.).

## Dependencias y por qué se usan

| Componente | Versión | Motivo |
|-----------|---------|--------|
| Java | 21 | Lenguaje estándar para Selenium y Cucumber. |
| Maven | 3.9+ | Gestiona dependencias, empaquetado y perfiles de ejecución. |
| Selenium WebDriver | 4.14.1 | API para interactuar con Chrome y manipular la UI del portal Altoro. |
| WebDriverManager | 6.1.0 | Alinea automáticamente la versión de ChromeDriver con el navegador instalado y gestiona la caché local. |
| Cucumber JVM | 7.14.0 | Motor BDD; incluye `cucumber-java`, `cucumber-picocontainer` y el engine para JUnit Platform. |
| JUnit Platform Suite | 1.10.0 | Orquestador que permite ejecutar Cucumber dentro del ecosistema JUnit 5. |
| JUnit Jupiter | 5.10.0 | API de aserciones empleada en los steps (`assertTrue`, `assertFalse`). |
| SLF4J + Logback | 2.0.9 / 1.5.19 | Logging consistente en hooks, driver y páginas. |
| Apache POI | 5.4.0 | Lectura del Excel `testData/data.xlsx` mediante `ExcelUtils`. |
| Apache Commons Lang | 3.18.0 | Utilidades auxiliares (por ejemplo, normalización de strings en los steps). |
| Jackson Databind | 2.15.3 | Dependencia transitiva requerida por los reportes/json de Cucumber. |
| Masterthought Cucumber Reporting | 5.7.4 | Genera dashboards HTML ricos a partir del `cucumber.json`. |

## Configuración y parametrización

### `config/defaults.properties`

| Clave | Descripción |
|-------|-------------|
| `app.baseUrl` | URL base del ambiente a probar (`https://demo.testfire.net`). |
| `app.username` / `app.password` | Credenciales por defecto que utilizan los flujos autenticados cuando el Excel no provee usuario/clave. |
| `timeout.seconds` | Timeout (segundos) que `BasePage` usa para `WebDriverWait`. |
| `navigation.retry.maxAttempts` / `navigation.retry.delay.millis` | Controla cuántos reintentos y cada cuánto se vuelve a llamar a `driver.get(...)` cuando hay errores de navegación. |
| `browser` | Navegador actual (Chrome). Permite abrir el camino a nuevos navegadores si se amplía `Driver`. |
| `browser.headless` | `true` para correr Chrome sin UI (`Driver` agrega `--headless=new`). |
| `scenario.excel.path` | Ruta (classpath o absoluta) hacia el workbook de datos. |

`Config` resuelve cualquier propiedad con el siguiente orden: **System property (`-Dclave=valor`) → Variable de entorno (clave en mayúsculas con `_`) → `config/defaults.properties`**. Ejemplo: `mvn -Dapp.baseUrl=https://qa.testfire.net test` apunta todos los escenarios al ambiente QA sin tocar archivos.

### Otros archivos de configuración

- `config/cucumber.properties`: hoy sólo habilita/desactiva el publish de Cucumber Cloud (`cucumber.publish.enabled=true` + `cucumber.publish.quiet=true`). El resto del glue/plugins está en `RunCucumberTest`.
- `config/junit-platform.properties`: fija `cucumber.execution.parallel.enabled=false`. Cambiarlo a `true` exige volver `Driver` y `Config.pagina(...)` tolerantes a múltiples hilos (por ejemplo usando `ThreadLocal` por escenario para todo el estado).

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
| Ejecutar en modo headless | `mvn test -Dbrowser.headless=true` |

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
- `transferencia.feature`: transferencias exitosas y validaciones de alertas (mismas cuentas, montos inválidos) parametrizadas desde la hoja `Transferencias`.
- `solicitud_tarjeta.feature`: flujos de solicitud aprobada/rechazada según la contraseña de confirmación.
- `feedback.feature`: envío de comentarios válidos y validación de errores en correos.

## Buenas prácticas y siguientes pasos

1. **Nuevas páginas:** cree una clase en `org.example.pages`, reciba `WebDriver` en el constructor, extienda de `BasePage` y exponga métodos semánticos. Instánciela desde los steps con `Config.pagina(MiNuevaPage.class)` para reutilizar el mismo objeto dentro del escenario.
2. **Nuevos datos:** agregue filas al Excel con un `dataId` único y use ese identificador en la tabla de Examples. Mantenga encabezados en castellano consistente.
3. **Tags:** utilice etiquetas (`@Login`, `@Smoke`, `@TransferenciaNegativa`) para dividir pipelines o suites rápidas.
4. **Esperas:** reemplace cualquier `Thread.sleep` por métodos de `BasePage` (`waitForVisibility`, `clickWithRetry`) para estabilidad.
5. **CI/CD:** publique los artefactos `target/cucumber-reports`, `target/cucumber-report-html` y `target/cucumber.json`. Configure el job para cargar screenshots como attachments.
6. **Escalabilidad:** si se requiere paralelismo, active `cucumber.execution.parallel.enabled=true` y mantenga `Driver`/`Config.pagina(...)` aislados por hilo (ThreadLocal). También evalúe usar `scenario.excel.path` distintos por pipeline para aislar datos.

Con esta guía un nuevo integrante puede comprender rápidamente la responsabilidad de cada componente, cómo fluye la información desde los features hasta los Page Objects y cómo ejecutar o extender la suite.
