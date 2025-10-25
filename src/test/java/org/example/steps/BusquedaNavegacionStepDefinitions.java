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

public class BusquedaNavegacionStepDefinitions {

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

    @Given("el usuario está en la página principal en la URL {string}")
    public void elUsuarioEstaEnLaPaginaPrincipalEnLaURL(String url) {
        driver.manage().deleteAllCookies();
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("query")));
    }

    @When("utiliza la función de búsqueda en el campo {string} e ingresa el término {string}")
    public void utilizaLaFuncionDeBusquedaEnElCampoEIngresaElTermino(String xpath, String termino) {
        WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        campoBusqueda.clear();
        campoBusqueda.sendKeys(termino);
    }

    @And("pulsa el botón de búsqueda {string}")
    public void pulsaElBotonDeBusqueda(String xpath) {
        WebElement botonBusqueda = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        String urlActual = driver.getCurrentUrl();
        botonBusqueda.click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("search.jsp"),
                ExpectedConditions.not(ExpectedConditions.urlToBe(urlActual))
        ));
    }

    @Then("el título de resultados {string} es visible")
    public void elTituloDeResultadosEsVisible(String xpath) {
        WebElement tituloResultados = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        assertTrue(tituloResultados.isDisplayed(), "No se visualizó el título de resultados esperado.");
    }

    @When("navega a la sección mediante el enlace {string}")
    public void navegaALaSeccionMedianteElEnlace(String xpath) {
        WebElement enlaceSeccion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        String urlActual = driver.getCurrentUrl();
        enlaceSeccion.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlActual)));
    }

    @Then("el encabezado {string} contiene {string}")
    public void elEncabezadoContiene(String xpath, String textoEsperado) {
        WebElement encabezado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        String texto = encabezado.getText().trim();
        assertTrue(texto.contains(textoEsperado), "El encabezado no contiene el texto esperado: " + textoEsperado);
    }
}
