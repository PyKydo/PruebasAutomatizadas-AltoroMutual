# Pruebas Automatizadas - Altoro Mutual

Suite de pruebas E2E en Java 21 + Cucumber que valida los flujos críticos del portal bancario de demostración Altoro Mutual. La solución adopta Page Object Model, inyección de dependencias con PicoContainer y reporting enriquecido para ofrecer una base mantenible y extensible.

## Tabla de contenido

1. [Sistema bajo prueba](#sistema-bajo-prueba)
2. [Arquitectura de la solución](#arquitectura-de-la-solución)
3. [Dependencias y herramientas](#dependencias-y-herramientas)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Cobertura funcional](#cobertura-funcional)
6. [Datos y configuración](#datos-y-configuración)
7. [Ejecución de pruebas](#ejecución-de-pruebas)
8. [Reportes y evidencias](#reportes-y-evidencias)
9. [Buenas prácticas y extensibilidad](#buenas-prácticas-y-extensibilidad)

## Sistema bajo prueba

- **URL:** [https://demo.testfire.net](https://demo.testfire.net)
- **Dominio:** banca en línea de ejemplo operada por Altoro Mutual.
- **Flujos validados:** autenticación, búsqueda y navegación, consulta de saldos, transferencias internas, solicitud de tarjeta Gold Visa y envío de feedback.

## Arquitectura de la solución

- **Page Object Model:** cada vista relevante expone acciones de alto nivel y hereda de `BasePage`, que centraliza navegación robusta (`navigateWithRetry`) y utilidades de espera.
- **PicoContainer/TestContext:** cada escenario recibe sus dependencias (WebDriver, Pages, data providers) mediante inyección en el constructor de los Steps, eliminando estados estáticos.
- **DriverFactory + Hooks:** las instancias de Chrome se crean endurecidas (modo incógnito, sin notificaciones). `Hooks` inicializa y cierra el navegador por escenario y adjunta capturas en caso de fallo.
- **Configuración centralizada:** `ConfigLoader` consume `src/test/resources/config/test.properties` para parámetros de entorno, credenciales y timeouts.
- **Datos externos:** validaciones dinámicas (por ejemplo, mensajes de transferencia) viven en `src/test/resources/testData/transfer-alerts.json` y se leen con `JsonDataProvider`.
- **Ejecución secuencial:** `junit-platform.properties` mantiene la paralelización desactivada para garantizar un único navegador por escenario.

## Dependencias y herramientas

| Componente | Versión | Uso |
|-----------|---------|-----|
| Java | 21 | Lenguaje principal |
| Maven | 3.9+ | Gestión de dependencias y ciclo de vida |
| Selenium WebDriver | 4.14.1 | Automatización de navegador |
| WebDriverManager | 5.5.3 | Aprovisionamiento automático de Chromedriver |
| Cucumber JVM | 7.14.0 | BDD + glue code (`cucumber-java`, `cucumber-junit-platform-engine`, `cucumber-picocontainer`) |
| JUnit Platform | 1.10.0 | Orquestador de pruebas |
| SLF4J + Logback | 2.0.9 / 1.4.11 | Logging uniforme |
| Jackson Databind | 2.15.3 | Lectura de JSON |
| Masterthought Report Plugin | 5.7.4 | Reporte HTML avanzado desde `cucumber.json` |

## Estructura del proyecto

```text
Prueba 2/
├─ pom.xml
├─ README.md
├─ src
│  └─ test
│     ├─ java
│     │  ├─ org/example/hooks/Hooks.java
│     │  ├─ org/example/pages/*.java
│     │  ├─ org/example/runners/RunCucumberTest.java
│     │  ├─ org/example/steps/*.java
│     │  └─ org/example/utils/
│     │        ├─ ConfigLoader.java
│     │        ├─ DriverFactory.java
│     │        ├─ JsonDataProvider.java
│     │        └─ TestContext.java
│     └─ resources
│           ├─ config/test.properties
│           ├─ cucumber.properties
│           ├─ junit-platform.properties
│           ├─ features/*.feature
│           └─ testData/transfer-alerts.json
└─ target/
    ├─ cucumber-reports/
    ├─ cucumber-report-html/
    └─ cucumber.json
```

## Cobertura funcional

### `busqueda_navegacion.feature`

- Busca el término "contact" y valida los resultados mostrados.
- Navega a secciones "Personal" y "Small Business" asegurando encabezados correctos.

### `login.feature`

- Autenticación con `jsmith` y `admin`.
- Validación de errores para credenciales vacías o incorrectas.

### `consulta_saldo.feature`

- Login orientado a lectura de saldos.
- Consulta de historial para las cuentas `800002` y `800003`, verificando encabezados y montos.

### `transferencia.feature`

- Transferencias exitosas entre cuentas de ahorro y tarjeta de crédito.
- Mensajes de alerta cuando origen = destino o se ingresa un monto inválido (parametrizado vía JSON).

### `solicitud_tarjeta.feature`

- Solicitud de la tarjeta Altoro Mutual Gold Visa.
- Respuesta aprobada o rechazada según la contraseña de confirmación ingresada.

### `feedback.feature`

- Envío exitoso de comentarios.
- Validación de errores para correos mal formados.

## Datos y configuración

- **`config/test.properties`:** contiene `app.baseUrl`, credenciales (`app.user`, `app.password`), navegador (`app.browser`) y `timeouts.*`. Cada valor puede sobrescribirse con `-Dapp.baseUrl=...` o variables de entorno.
- **`testData/transfer-alerts.json`:** diccionario clave → mensaje esperado para escenarios negativos de transferencias (por ejemplo, `sameAccount`, `invalidAmount`).
- **`cucumber.properties`:** define `cucumber.plugin`, `cucumber.glue`, `cucumber.features` y desactiva la publicación externa (`cucumber.publish.enabled=false`).
- **`junit-platform.properties`:** establece `cucumber.execution.parallel.enabled=false` para ejecuciones secuenciales y controladas.

## Ejecución de pruebas

### Requisitos previos

1. Java 21 instalado (`java -version`).
2. Maven 3.9+ (`mvn -v`).
3. Google Chrome actualizado (WebDriverManager descarga el driver compatible).
4. Acceso a Internet para alcanzar `demo.testfire.net` y descargar dependencias.

### Comandos principales

| Caso de uso | Comando |
|-------------|---------|
| Ejecutar toda la suite | `mvn test` |
| Ejecutar por etiqueta | `mvn test -Dcucumber.filter.tags="@Transferencia"` |
| Generar reporte Masterthought | `mvn verify` |

Los navegadores se abren y cierran por escenario en los hooks `@Before`/`@After`. Ante fallos, Maven marca `BUILD FAILURE`, pero los reportes (JSON y HTML) siguen generándose para su consulta.

### Configuración de entornos

- Para apuntar a otra URL o credenciales, use `mvn test -Dapp.baseUrl=https://qa.testfire.net` o defina variables de entorno (`setx APP_BASE_URL ...`).
- `ConfigLoader` primero lee variables del sistema y luego el archivo, permitiendo una jerarquía clara de overrides.

## Reportes y evidencias

- **Cucumber HTML:** `target/cucumber-reports/report.html`.
- **Cucumber JSON:** `target/cucumber.json`, insumo para pipelines o herramientas externas.
- **Masterthought dashboard:** `target/cucumber-report-html/cucumber-html-reports/index.html` con métricas, tendencias y navegación por escenario.
- **Screenshots:** los hooks capturan evidencia (`image/png`) cuando un escenario falla y la adjuntan al reporte.
- **JUnit XML:** `target/cucumber-results.xml`, útil para integraciones con CI.

## Buenas prácticas y extensibilidad

1. **Nuevas páginas:** cree una clase en `org.example.pages`, inyecte `WebDriver` vía constructor y exponga métodos semánticos; recupérela en los Steps usando `context.getPage(Pagina.class)`.
2. **Datos externos:** agregue archivos JSON o `.properties` en `src/test/resources/testData/` y documente el formato para mantener escenarios limpios.
3. **Tags y filtrado:** utilice etiquetas (`@Login`, `@Transferencia`, etc.) para ejecuciones focalizadas o pipelines paralelos.
4. **Esperas explícitas:** reemplace `Thread.sleep` con utilidades ya presentes en `BasePage` para mayor estabilidad.
5. **CI/CD:** incluya `mvn test` y `mvn verify` en el pipeline y publique los artefactos de `target/cucumber-reports` y `target/cucumber-report-html`.
6. **Escalabilidad:** si se requiere paralelismo, active `cucumber.execution.parallel.enabled=true` y adapte `DriverFactory` a un enfoque `ThreadLocal`.
