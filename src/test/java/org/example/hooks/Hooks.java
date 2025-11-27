package org.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.utils.TestContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);
    private final TestContext testContext;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("Iniciando escenario: {}", scenario.getName());
        testContext.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        LOGGER.info("Finalizando escenario: {} - Estado: {}", scenario.getName(), scenario.getStatus());
        if (scenario.isFailed()) {
            attachScreenshot(scenario);
        }
        testContext.cleanupDriver();
    }

    private void attachScreenshot(Scenario scenario) {
        try {
            WebDriver driver = testContext.getDriver();
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
