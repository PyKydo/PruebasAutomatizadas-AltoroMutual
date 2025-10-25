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

public class FeedbackStepDefinitions {

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

    @Given("el usuario está en la página de feedback en la URL {string}")
    public void elUsuarioEstaEnLaPaginaDeFeedbackEnLaURL(String url) {
        driver.manage().deleteAllCookies();
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@name='cmt']")));
    }

    @When("completa el campo nombre {string} con {string}")
    public void completaElCampoNombreCon(String xpath, String valor) {
        completarCampoTexto(xpath, valor);
    }

    @And("completa el campo email {string} con {string}")
    public void completaElCampoEmailCon(String xpath, String valor) {
        completarCampoTexto(xpath, valor);
    }

    @And("completa el campo asunto {string} con {string}")
    public void completaElCampoAsuntoCon(String xpath, String valor) {
        completarCampoTexto(xpath, valor);
    }

    @And("completa el campo mensaje {string} con {string}")
    public void completaElCampoMensajeCon(String xpath, String valor) {
        completarCampoTexto(xpath, valor);
    }

    @And("envía el formulario con el botón {string}")
    public void enviaElFormularioConElBoton(String xpath) {
        WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        String urlActual = driver.getCurrentUrl();
        botonEnviar.click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("feedbacksuccess.jsp"),
                ExpectedConditions.not(ExpectedConditions.urlToBe(urlActual))
        ));
    }

    @Then("el mensaje de feedback en {string} contiene {string}")
    public void elMensajeDeFeedbackEnContiene(String xpath, String textoEsperado) {
        WebElement contenedor = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        String texto = contenedor.getText();
        String textoNormalizado = texto == null ? "" : texto.toLowerCase();
        String esperadoNormalizado = textoEsperado == null ? "" : textoEsperado.toLowerCase();
        assertTrue(textoNormalizado.contains(esperadoNormalizado),
                "El texto mostrado no contiene lo esperado: " + textoEsperado);
    }

    private void completarCampoTexto(String xpath, String valor) {
        WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        campo.clear();
        campo.sendKeys(valor);
    }
}
