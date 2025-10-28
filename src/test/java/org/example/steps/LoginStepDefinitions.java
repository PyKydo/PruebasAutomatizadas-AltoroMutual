package org.example.steps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStepDefinitions {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("el usuario está en la página de login en la URL {string}")
    public void elUsuarioEstaEnLaPaginaDeLogin(String url) {
        try {
            driver.get(url);
            try {
                Alert alert = driver.switchTo().alert();
                System.out.println("Alerta encontrada al cargar la página: " + alert.getText());
                alert.accept();
            } catch (NoAlertPresentException e) {
            }
        } catch (org.openqa.selenium.UnhandledAlertException e) {
            try {
                Alert alert = driver.switchTo().alert();
                System.out.println("Alerta no manejada encontrada: " + alert.getText());
                alert.accept();
                driver.get(url);
            } catch (NoAlertPresentException ex) {
            }
        }
    }

    @When("completa el campo usuario {string} con {string}")
    public void completaElCampoUsuario(String xpath, String valor) {
        if (valor != null && !valor.isEmpty()) {
            WebElement campoUsuario = driver.findElement(By.xpath(xpath));
            campoUsuario.sendKeys(valor);
        }
    }

    @When("completa el campo contraseña {string} con {string}")
    public void completaElCampoContrasena(String xpath, String valor) {
        if (valor != null && !valor.isEmpty()) {
            WebElement campoContrasena = driver.findElement(By.xpath(xpath));
            campoContrasena.sendKeys(valor);
        }
    }

    @When("pulsa el botón de login {string}")
    public void pulsaElBotonDeLogin(String xpath) {
        WebElement botonLogin = driver.findElement(By.xpath(xpath));
        botonLogin.click();
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {
        }
    }

    @Then("se muestra un mensaje en {string}")
    public void seMuestraUnMensaje(String xpath) {
        try {
            WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            assertTrue(mensaje.isDisplayed(), "No se mostró el mensaje esperado.");
        } catch (Exception e) {
            System.out.println("No se pudo encontrar el elemento: " + xpath);
            try {
                Alert alert = driver.switchTo().alert();
                System.out.println("Alerta encontrada: " + alert.getText());
                alert.accept();
            } catch (NoAlertPresentException ex) {
            }
            assertTrue(false, "Fallo al verificar el mensaje en: " + xpath);
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
