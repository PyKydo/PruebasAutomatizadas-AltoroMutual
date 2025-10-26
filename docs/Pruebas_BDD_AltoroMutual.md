# Pruebas automatizadas (BDD) — Altoro Mutual

Documento de apoyo académico para comprender, trazar y ejecutar los casos de prueba automatizados del proyecto. Se basa en los artefactos existentes (features Gherkin y step definitions) y sigue buenas prácticas de documentación de pruebas.

---

## Contexto y alcance

- Sistema bajo prueba: sitio demo Altoro Mutual (https://demo.testfire.net)
- Enfoque: BDD con Cucumber + Selenium WebDriver (Chrome) + JUnit Platform
- Lenguaje/stack: Java 21, Maven
- Ubicación de artefactos principales:
  - Features (Gherkin): `src/test/resources/features/*.feature`
  - Step Definitions: `src/test/java/org/example/steps/*.java`
  - Runner: `src/test/java/org/example/RunCucumberTest.java`
  - Reportes JUnit XML: `target/surefire-reports/`

Dependencias relevantes (pom.xml):

- io.cucumber:cucumber-java 7.14.0
- io.cucumber:cucumber-junit-platform-engine 7.14.0
- org.junit.jupiter:junit-jupiter-api 5.10.0
- org.junit.platform:junit-platform-suite 1.10.0
- org.seleniumhq.selenium:selenium-java 4.14.1
- io.github.bonigarcia:webdrivermanager 5.5.3

## Resumen cuantitativo (conteo)

- Features: 6
- Backgrounds: 6
- Tags únicos: 6 (@Login, @Transferencia, @ConsultaSaldo, @Feedback, @SolicitudTarjeta, @Busqueda)
- Escenarios definidos: 9 (6 Scenario Outline, 3 Scenario simples)
- Casos de prueba ejecutables (expandidos): 18
- Step Definitions (Java): 36 métodos anotados (Given/When/Then/And)
- Última ejecución JUnit: 18 corridos, 1 fallo, 1 error, 16 aprobados

Detalle por feature:

| Feature | Tag | Escenarios (def.) | Outlines | Casos (expandidos) | Background |
|---|---|---:|---:|---:|:---:|
| Autenticación de usuario en el sistema | @Login | 1 | 1 | 5 | Sí |
| Transferencia de fondos entre cuentas | @Transferencia | 2 | 2 | 4 | Sí |
| Consulta de saldos y movimientos de cuentas | @ConsultaSaldo | 1 | 1 | 2 | Sí |
| Envío de feedback al banco | @Feedback | 1 | 1 | 2 | Sí |
| Solicitud de la tarjeta Altoro Mutual Gold Visa | @SolicitudTarjeta | 1 | 1 | 2 | Sí |
| Búsqueda y navegación de contenido | @Busqueda | 3 | 0 | 3 | Sí |

Nota: Casos "expandidos" = suma de filas en Examples de los Scenario Outline + 1 por cada Scenario simple. Los 18 coinciden con el total reportado por Surefire.

---

## Matriz de trazabilidad

Relación de requerimientos funcionales (derivados de las features) con escenarios y automatizaciones.

| ID Req | Requerimiento (resumen) | Feature (tag) | Escenario(s) | Step Definitions clave | Evidencia/Reporte |
|---|---|---|---|---|---|
| REQ-01 | El usuario puede autenticarse para acceder al sitio. | Autenticación de usuario (@Login) | SCN-LOGIN-01: Inicio de sesión (Outline, positivo/negativo) | `LoginStepDefinitions` — Given URL login; completar usuario/contraseña; clic login; ver mensaje | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |
| REQ-02 | El usuario autenticado puede transferir fondos entre cuentas. | Transferencia de fondos (@Transferencia) | SCN-TRF-01: Transferencia entre cuentas; SCN-TRF-02: Validaciones de alerta | `TransferenciaStepDefinitions` — login inicial; seleccionar from/to; monto; confirmar; verificar mensaje/alerta | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |
| REQ-03 | El usuario autenticado puede consultar saldos e historial. | Consulta de saldos (@ConsultaSaldo) | SCN-CS-01: Visualización historial y balance (Outline por cuenta) | `ConsultaSaldoStepDefinitions` — login; seleccionar cuenta; consultar; verificar encabezado y balance | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |
| REQ-04 | El usuario puede enviar feedback al banco. | Envío de feedback (@Feedback) | SCN-FBK-01: Verificación formulario (positivo/validación email) | `FeedbackStepDefinitions` — completar campos; enviar; verificar mensaje | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |
| REQ-05 | El usuario autenticado puede solicitar tarjeta Gold Visa. | Solicitud tarjeta (@SolicitudTarjeta) | SCN-CARD-01: Resultado de solicitud (clave correcta/incorrecta) | `SolicitudTarjetaStepDefinitions` — login; navegar a apply; ingresar clave; enviar; verificar mensaje | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |
| REQ-06 | El usuario puede buscar y navegar por secciones del sitio. | Búsqueda y navegación (@Busqueda) | SCN-NAV-01: Búsqueda; SCN-NAV-02: Navegación Personal; SCN-NAV-03: Navegación Small Business | `BusquedaNavegacionStepDefinitions` — visitar home; buscar; navegar; verificar encabezados | `surefire-reports/TEST-org.example.RunCucumberTest.xml` |

Notas:

- Los IDs de requerimiento (REQ-XX) son asignados para la trazabilidad documental y referencian de forma 1:N a los escenarios automatizados.
- Las etiquetas de feature (@Login, @Transferencia, etc.) permiten filtrar ejecución por tag si se agrega configuración de Cucumber.

---

## Codificación de escenarios de prueba (Gherkin)

A continuación, fragmentos representativos (se mantienen en los archivos `.feature`).

### Login — `features/login.feature`

```gherkin
@Login
Feature: Autenticación de usuario en el sistema
  Background:
    Given el usuario está en la página de login en la URL "https://demo.testfire.net/login.jsp"

  Scenario Outline: Inicio de sesión
    When completa el campo usuario "//input[@id='uid']" con "<usuario>"
    And completa el campo contraseña "//input[@id='passw']" con "<contraseña>"
    And pulsa el botón de login "//input[@name='btnSubmit' and @type='submit']"
    Then se muestra un mensaje en "<xpath_mensaje>"

    Examples:
      | usuario  | contraseña | xpath_mensaje                                  |
      | jsmith   | demo1234   | //a[normalize-space()='Sign Off']              |
      | admin    | admin      | //a[normalize-space()='Sign Off']              |
      | fakeuser | fakepass   | //span[@id='_ctl0__ctl0_Content_Main_message'] |
```

### Transferencia — `features/transferencia.feature`

```gherkin
@Transferencia
Feature: Transferencia de fondos entre cuentas
  Background:
    Given el usuario ha iniciado sesión
    And accede a la sección de transferencias mediante el enlace "//a[@id='MenuHyperLink3']"

  Scenario Outline: Transferencia de fondos entre diferentes cuentas
    When selecciona la cuenta origen "<cuenta_origen>" en el selector "//select[@id='fromAccount']"
    And selecciona la cuenta destino "<cuenta_destino>" en el selector "//select[@id='toAccount']"
    And ingresa "<monto>" en el campo de monto "//input[@id='transferAmount']"
    And confirma la transferencia con el botón "//input[@id='transfer']"
    Then el mensaje de transferencia en "//span[@id='_ctl0__ctl0_Content_Main_postResp']" contiene "<mensaje_esperado>"
```

### Consulta de saldo — `features/consulta_saldo.feature`

```gherkin
@ConsultaSaldo
Feature: Consulta de saldos y movimientos de cuentas
  Background:
    Given el usuario inicia sesión para consultar saldos

  Scenario Outline: Visualización del historial y balance de la cuenta
    When selecciona la cuenta "<cuenta>" desde el panel principal
    And confirma la consulta del historial de la cuenta
    Then el encabezado de historial incluye "<cuenta>"
    And el detalle de balance presenta un monto disponible
```

### Feedback — `features/feedback.feature`

```gherkin
@Feedback
Feature: Envío de feedback al banco
  Background:
    Given el usuario está en la página de feedback en la URL "https://demo.testfire.net/feedback.jsp"

  Scenario Outline: Verificación del formulario de envío de feedback
    When completa el campo nombre "//input[@name='name']" con "<nombre>"
    And completa el campo email "//input[@name='email_addr']" con "<email>"
    And completa el campo asunto "//input[@name='subject']" con "<asunto>"
    And completa el campo mensaje "//textarea[@name='comments']" con "<mensaje>"
    And envía el formulario con el botón "//input[@name='submit' and @type='submit']"
    Then el mensaje de feedback en "//body/table/tbody/tr[2]/td[2]/div/p" contiene "<mensaje_esperado>"
```

### Solicitud tarjeta — `features/solicitud_tarjeta.feature`

```gherkin
@SolicitudTarjeta
Feature: Solicitud de la tarjeta Altoro Mutual Gold Visa
  Background:
    Given el usuario inicia sesión para la solicitud de tarjeta
    And navega a la sección de solicitud mediante el enlace "//a[@href='apply.jsp']"

  Scenario Outline: Resultado de la solicitud de tarjeta
    When ingresa la contraseña de solicitud "<clave>" en el campo "//input[@name='passwd']"
    And envía la solicitud con el botón "//input[@name='Submit' and @type='submit']"
    Then se muestra el mensaje de solicitud en "<xpath_mensaje>" con "<mensaje_esperado>"
```

### Búsqueda y navegación — `features/busqueda_navegacion.feature`

```gherkin
@Busqueda
Feature: Búsqueda y navegación de contenido
  Background:
    Given el usuario está en la página principal en la URL "https://demo.testfire.net/"

  Scenario: Búsqueda de información
    When utiliza la función de búsqueda en el campo "//input[@id='query']" e ingresa el término "contact"
    And pulsa el botón de búsqueda "//input[@type='submit' and @value='Go']"
    Then el título de resultados "//h1[normalize-space()='Search Results']" es visible
```

---

## Condiciones de aceptación (por escenario)

Las condiciones de aceptación reflejan los Then/And de cada escenario e incluyen consideraciones negativas donde aplica.

- SCN-LOGIN-01 (Inicio de sesión)
  - Dado usuario en `login.jsp`, cuando ingresa credenciales y pulsa Login, entonces:
    - Para credenciales válidas, el enlace "Sign Off" es visible.
    - Para credenciales inválidas o vacías, aparece el mensaje de error en `#_ctl0__ctl0_Content_Main_message`.

- SCN-TRF-01 (Transferencia entre cuentas)
  - Dado usuario autenticado en Transferencias, cuando selecciona cuentas distintas y monto > 0 y confirma, entonces el texto de confirmación contiene el número de cuenta de origen.

- SCN-TRF-02 (Validaciones de alerta)
  - Si origen = destino, aparece alerta: "From Account and To Account fields cannot be the same." y se acepta.
  - Si monto ≤ 0 o no numérico, aparece alerta: "Transfer Amount must be a number greater than 0." y se acepta.

- SCN-CS-01 (Consulta de historial y balance)
  - Tras seleccionar una cuenta y consultar, el `<h1>` incluye el identificador de la cuenta y el balance mostrado no está vacío.

- SCN-FBK-01 (Feedback)
  - Tras completar y enviar el formulario, el párrafo de confirmación contiene el nombre cuando el correo es válido.
  - Con email inválido, el texto indica el error de email incorrecto.

- SCN-CARD-01 (Solicitud tarjeta)
  - Con clave correcta, el mensaje confirma emisión de la tarjeta con límite y APR.
  - Con clave incorrecta, el mensaje indica "Login Failed..." en el contenedor de mensaje correspondiente.

- SCN-NAV-01/02/03 (Búsqueda/Navegación)
  - La búsqueda de "contact" lleva a página de resultados con `<h1>` esperado.
  - La navegación por enlaces de encabezado cambia de URL y los encabezados contienen "Personal" o "Small Business" según corresponda.

---

## Casos de prueba (BDD) y configuración de ejecución

- Runner JUnit Platform: `RunCucumberTest` con:
  - `@IncludeEngines("cucumber")`
  - `@SelectClasspathResource("features")`
  - `@ConfigurationParameter(GLUE = "org.example.steps")`
- Navegador: Chrome administrado por WebDriverManager.
- Sin hooks globales de Cucumber adicionales; cada clase usa `@BeforeAll`/`@AfterAll` para instanciar y cerrar `ChromeDriver`.

### Cómo ejecutar (Windows PowerShell)

```powershell
# Ejecutar todos los features
mvn -q -DskipTests=false test

# (Opcional) Ejecutar filtrando por tag — requiere configurar filtros en el runner o properties
# mvn -Dcucumber.filter.tags="@Login" test
```

Recomendaciones de estabilidad:

- Alinear versión de Selenium DevTools con la versión de Chrome cuando aparezcan advertencias de CDP (ver logs).
- Usar esperas explícitas adecuadas y evitar `NoAlertPresentException` no manejadas.
- Considerar un `@Before`/`@After` por escenario para aislar contexto entre escenarios.

---

## Evidencia de resultados (última ejecución disponible)

Fuente: `target/surefire-reports/TEST-org.example.RunCucumberTest.xml` (25-10-2025)

- Total: 18 | Fallos: 1 | Errores: 1 | Skips: 0 | Tiempo: 82.37 s
- Fallas destacadas:
  - Consulta saldo (SCN-CS-01, Example 800002): Timeout esperando `//h1` visible.
  - Login (SCN-LOGIN-01, credenciales válidas): no se encontró `Sign Off` al verificar.

Ruta de reportes:

- Texto: `target/surefire-reports/org.example.RunCucumberTest.txt`
- XML JUnit: `target/surefire-reports/TEST-org.example.RunCucumberTest.xml`

Sugerencias de mejora:

- Aumentar timeout o esperar a que la navegación concluya antes de validar `<h1>` en consulta de saldo.
- En login, esperar explícitamente el menú autenticado o redirección antes de buscar "Sign Off".

---

## Plantilla: Escenario de prueba con evidencia

Use esta plantilla para documentar nuevos escenarios BDD junto con la evidencia de ejecución.

```markdown
### [ID] Nombre del escenario

- Requerimiento(s): REQ-XX[, REQ-YY]
- Feature/tag: [Nombre de feature] (@Tag)
- Prioridad: Alta | Media | Baja
- Datos de prueba: describir valores/significancia (o Examples si Outline)
- Precondiciones: estado del sistema/datos; URL inicial
- Pasos (Gherkin) — ejemplo:
    Scenario[: Outline] Nombre
      Given ...
      When ...
      Then ...
- Condiciones de aceptación: bullets claros y verificables
- Automatización (Step Definitions): clases/métodos relevantes
- Evidencia:
  - Resultado: Passed | Failed | Error
  - Capturas: docs/evidencias/escenario-id-01.png (opcional)
  - Logs: fragmento relevante o enlace a target/surefire-reports/...
- Notas/Riesgos: sincronización, dependencias externas, fragilidad de locators
```

Carpeta sugerida para evidencias (si se capturan imágenes): `docs/evidencias/`.

---

## Anexo: Mapeo alto nivel Step Definitions → Intención

- `LoginStepDefinitions`: navegación a login; ingreso credenciales; clic; verificación de elemento por XPath.
- `TransferenciaStepDefinitions`: login embebido; selección en `<select>` (from/to); monto; confirmación; validación de mensaje o `Alert`.
- `ConsultaSaldoStepDefinitions`: login; selección de cuenta; navegación; verificación de `<h1>` y celda de "Ending balance".
- `FeedbackStepDefinitions`: navegación a `feedback.jsp`; completado de campos; envío; validación de texto.
- `SolicitudTarjetaStepDefinitions`: login; navegación a `apply.jsp`; ingreso de clave; envío; verificación de mensaje.
- `BusquedaNavegacionStepDefinitions`: navegación a home; búsqueda; navegación por enlaces de encabezado; verificación de encabezados.

---

## Referencias

- Código fuente del proyecto (esta repo)
- Documentación oficial:
  - Cucumber for Java (JUnit Platform Engine)
  - Selenium WebDriver (Java)
  - JUnit 5 (Platform Suite)

