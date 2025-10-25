package org.example.steps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferenciaStepDefinitions {

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

    @Given("el usuario ha iniciado sesión")
    public void elUsuarioHaIniciadoSesion() {
        driver.get("https://demo.testfire.net/login.jsp");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("uid"))).sendKeys("jsmith");
        driver.findElement(By.id("passw")).sendKeys("demo1234");
        driver.findElement(By.name("btnSubmit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("MenuHyperLink3"))); // wait for the authenticated navigation menu
    }

    @And("accede a la sección de transferencias mediante el enlace {string}")
    public void accedeALaSeccionDeTransferenciasMedianteElEnlace(String xpath) {
        WebElement transferLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        transferLink.click();
    }

    @When("selecciona la cuenta origen {string} en el selector {string}")
    public void seleccionaLaCuentaOrigenEnElSelector(String cuenta, String xpath) {
        WebElement fromAccount = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        Select fromAccountSelect = new Select(fromAccount);
        fromAccountSelect.selectByValue(cuenta);
    }

    @And("selecciona la cuenta destino {string} en el selector {string}")
    public void seleccionaLaCuentaDestinoEnElSelector(String cuenta, String xpath) {
        WebElement toAccount = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        Select toAccountSelect = new Select(toAccount);
        toAccountSelect.selectByValue(cuenta);
    }

    @And("ingresa {string} en el campo de monto {string}")
    public void ingresaEnElCampoDeMonto(String monto, String xpath) {
        WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        amountField.clear();
        amountField.sendKeys(monto);
    }

    @And("confirma la transferencia con el botón {string}")
    public void confirmaLaTransferenciaConElBoton(String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    @Then("el mensaje de transferencia en {string} contiene {string}")
    public void elMensajeDeTransferenciaEnContiene(String xpath, String mensajeEsperado) {
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        String texto = message.getText().trim();
        assertTrue(texto.contains(mensajeEsperado), "El mensaje obtenido no contiene el texto esperado: " + mensajeEsperado);
    }

    @Then("se muestra una alerta de transferencia con el mensaje {string}")
    public void seMuestraUnaAlertaDeTransferenciaConElMensaje(String mensajeEsperado) {
        Alert alerta = wait.until(ExpectedConditions.alertIsPresent());
        String textoAlerta = alerta.getText();
        assertTrue(textoAlerta.contains(mensajeEsperado), "El texto de la alerta no coincide con lo esperado.");
        alerta.accept();
    }
}
