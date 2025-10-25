package org.example.steps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolicitudTarjetaStepDefinitions {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("el usuario inicia sesión para la solicitud de tarjeta")
    public void elUsuarioIniciaSesionParaLaSolicitudDeTarjeta() {
        driver.get("https://demo.testfire.net/login.jsp");
        WebElement usuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("uid")));
        usuario.clear();
        usuario.sendKeys("jsmith");
        WebElement contrasena = driver.findElement(By.id("passw"));
        contrasena.clear();
        contrasena.sendKeys("demo1234");
        driver.findElement(By.name("btnSubmit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("MenuHyperLink3")));
    }

    @And("navega a la sección de solicitud mediante el enlace {string}")
    public void navegaALaSeccionDeSolicitudMedianteElEnlace(String xpath) {
        WebElement enlaceSolicitud = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        enlaceSolicitud.click();
    }

    @When("ingresa la contraseña de solicitud {string} en el campo {string}")
    public void ingresaLaContrasenaDeSolicitudEnElCampo(String contrasena, String xpath) {
        WebElement campoContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        campoContrasena.clear();
        campoContrasena.sendKeys(contrasena);
    }

    @And("envía la solicitud con el botón {string}")
    public void enviaLaSolicitudConElBoton(String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    @Then("se muestra el mensaje de aprobación en {string} con {string}")
    public void seMuestraElMensajeDeAprobacionEnCon(String xpath, String mensajeEsperado) {
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        String texto = mensaje.getText().trim();
        assertTrue(texto.contains(mensajeEsperado), "El mensaje de aprobación obtenido no contiene el texto esperado.");
    }

    @Then("se muestra el mensaje de error de solicitud en {string} con {string}")
    public void seMuestraElMensajeDeErrorDeSolicitudEnCon(String xpath, String mensajeEsperado) {
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        String texto = mensaje.getText().trim();
        assertTrue(texto.contains(mensajeEsperado), "El mensaje de error obtenido no contiene el texto esperado.");
    }
}
