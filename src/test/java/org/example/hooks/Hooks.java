package org.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.utils.Driver;
import org.example.utils.Config;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);
    @Before
    public void iniciarEscenario(Scenario scenario) {
        LOGGER.info("Iniciando escenario: {}", scenario.getName());
        Driver.iniciar();
    }

    @After
    public void finalizarEscenario(Scenario scenario) {
        LOGGER.info("Finalizando escenario: {} - Estado: {}", scenario.getName(), scenario.getStatus());
        if (scenario.isFailed()) {
            adjuntarEvidencia(scenario);
        }
        Driver.cerrar();
        Config.limpiarCachePaginas();
    }

    private void adjuntarEvidencia(Scenario scenario) {
        try {
            WebDriver driver = Driver.driver();
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "failure-" + scenario.getName());
                LOGGER.info("Screenshot adjuntado para el escenario fallido {}", scenario.getName());
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo capturar la evidencia del escenario {}", scenario.getName(), e);
        }
    }
}
