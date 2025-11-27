package org.example.utils;

import org.example.pages.BasePage;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provee dependencias de escenario (driver y page objects) mediante PicoContainer.
 */
public class TestContext {

    private final Map<Class<?>, Object> pageCache = new ConcurrentHashMap<>();

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    public void initDriver() {
        DriverFactory.createDriver();
    }

    public void cleanupDriver() {
        try {
            DriverFactory.quitDriver();
        } finally {
            pageCache.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BasePage> T getPage(Class<T> pageType) {
        return (T) pageCache.computeIfAbsent(pageType, this::instantiatePage);
    }

    private BasePage instantiatePage(Class<?> pageType) {
        try {
            @SuppressWarnings("unchecked")
            Constructor<? extends BasePage> constructor = (Constructor<? extends BasePage>)
                    pageType.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return constructor.newInstance(getDriver());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("No se pudo crear la página " + pageType.getSimpleName(), e);
        }
    }
}
