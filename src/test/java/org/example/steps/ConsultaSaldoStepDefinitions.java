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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultaSaldoStepDefinitions {

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

    @Given("el usuario inicia sesión para consultar saldos")
    public void elUsuarioIniciaSesionParaConsultarSaldos() {
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

    @When("selecciona la cuenta {string} desde el panel principal")
    public void seleccionaLaCuentaDesdeElPanelPrincipal(String cuenta) {
        WebElement selectorCuentas = wait.until(ExpectedConditions.elementToBeClickable(By.id("listAccounts")));
        Select lista = new Select(selectorCuentas);
        lista.selectByValue(cuenta);
    }

    @And("confirma la consulta del historial de la cuenta")
    public void confirmaLaConsultaDelHistorialDeLaCuenta() {
        WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnGetAccount")));
        boton.click();
    }

    @Then("el encabezado de historial incluye {string}")
    public void elEncabezadoDeHistorialIncluye(String cuenta) {
        WebElement encabezado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1")));
        String texto = encabezado.getText();
        assertTrue(texto.contains(cuenta), "El encabezado no contiene el identificador de cuenta esperado.");
    }

    @And("el detalle de balance presenta un monto disponible")
    public void elDetalleDeBalancePresentaUnMontoDisponible() {
        WebElement monto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(),'Ending balance')]/following-sibling::td")));
        String textoMonto = monto.getText().trim();
        assertFalse(textoMonto.isEmpty(), "El monto de balance no debería estar vacío.");
    }
}
