package org.example.pages;

import org.example.utils.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    private final WebDriverWait wait;
    private final int navigationMaxAttempts;
    private final int navigationDelayMillis;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = Config.configInt("timeout.seconds", 10);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.navigationMaxAttempts = Config.configInt("navigation.retry.maxAttempts", 3);
        this.navigationDelayMillis = Config.configInt("navigation.retry.delay.millis", 2000);
    }

    protected WebElement esperarVisibilidad(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement esperarElementoClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void ingresarTexto(By locator, String value) {
        WebElement element = esperarVisibilidad(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected void hacerClick(By locator) {
        esperarElementoClickable(locator).click();
    }

    protected String obtenerTexto(By locator) {
        return esperarVisibilidad(locator).getText().trim();
    }

    protected boolean estaElementoPresente(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void navegarConReintentos(String url) {
        
        int attempts = 0;
        while (true) {
            try {
                driver.get(url);
                return;
            } catch (WebDriverException ex) {
                attempts++;
                if (attempts >= navigationMaxAttempts) {
                    throw ex;
                }
                pausarAntesDeReintentar();
            }
        }
    }

    private void pausarAntesDeReintentar() {
        try {
            Thread.sleep(navigationDelayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
