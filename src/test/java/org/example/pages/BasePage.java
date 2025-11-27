package org.example.pages;

import org.example.utils.ConfigLoader;
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
        int timeout = ConfigLoader.getInt("timeout.seconds", 10);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.navigationMaxAttempts = ConfigLoader.getInt("navigation.retry.maxAttempts", 3);
        this.navigationDelayMillis = ConfigLoader.getInt("navigation.retry.delay.millis", 2000);
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void type(By locator, String value) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void navigateWithRetry(String url) {
        
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
                sleepBeforeRetry();
            }
        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(navigationDelayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
