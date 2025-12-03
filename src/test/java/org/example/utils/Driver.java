package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public final class Driver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private Driver() {
    }

    public static void iniciar() {
        if (DRIVER.get() != null) {
            return;
        }
        WebDriver driver = crearDriver(Config.config("browser", "chrome"));
        if (!headlessEnabled()) {
            driver.manage().window().maximize();
        }
        DRIVER.set(driver);
        LOGGER.info("Driver inicializado{}", headlessEnabled() ? " en modo headless" : "");
    }

    public static WebDriver driver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("El WebDriver no está inicializado");
        }
        return driver;
    }

    public static void cerrar() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            LOGGER.info("Driver finalizado");
        }
    }

    private static WebDriver crearDriver(String browser) {
        switch (browser.toLowerCase(Locale.ROOT)) {
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito", "--disable-notifications", "--disable-popup-blocking",
                        "--disable-save-password-bubble", "--no-default-browser-check");
                if (headlessEnabled()) {
                    options.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu");
                }
                return new ChromeDriver(options);
        }
    }

    private static boolean headlessEnabled() {
        String flag = Config.config("browser.headless", "false");
        return "true".equalsIgnoreCase(flag) || "1".equals(flag);
    }
}
