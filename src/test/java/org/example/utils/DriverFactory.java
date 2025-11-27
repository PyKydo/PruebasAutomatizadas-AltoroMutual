package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona instancias de WebDriver por hilo de ejecución.
 */
public final class DriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void createDriver() {
        if (DRIVER.get() != null) {
            return;
        }
        String browser = ConfigLoader.getOrDefault("browser", "chrome");
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito", "--disable-notifications", "--disable-popup-blocking",
                        "--disable-save-password-bubble", "--no-default-browser-check");
                driver = new ChromeDriver(options);
                break;
        }
        driver.manage().window().maximize();
        DRIVER.set(driver);
        LOGGER.info("Driver inicializado para el navegador: {}", browser);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("El WebDriver no está inicializado. Asegúrese de ejecutar los Hooks.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            LOGGER.info("Driver finalizado correctamente");
        }
    }
}
