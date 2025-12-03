package org.example.utils;

import org.example.pages.BasePage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class Config {

        private static final Properties DEFAULTS = loadDefaults();

    private static final ThreadLocal<Map<Class<?>, Object>> PAGE_CACHE =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    private Config() {
    }

    public static String config(String key) {
        return Objects.requireNonNull(resolve(key, null), () -> "Propiedad no definida: " + key);
    }

    public static String config(String key, String fallback) {
        return resolve(key, fallback);
    }

    public static int configInt(String key, int fallback) {
        try {
            return Integer.parseInt(Objects.requireNonNull(resolve(key, null)).trim());
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }

    private static String resolve(String key, String fallback) {
        String sys = System.getProperty(key);
        if (hasText(sys)) {
            return sys;
        }
        String env = System.getenv(key.toUpperCase(Locale.ROOT).replace('.', '_'));
        if (hasText(env)) {
            return env;
        }
        String def = DEFAULTS.getProperty(key);
        return hasText(def) ? def : fallback;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @SuppressWarnings("unchecked")
    public static <T extends BasePage> T pagina(Class<T> pageType) {
        return (T) PAGE_CACHE.get().computeIfAbsent(pageType, Config::instanciarPagina);
    }

    private static BasePage instanciarPagina(Class<?> pageType) {
        try {
            Constructor<?> constructor = pageType.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return (BasePage) constructor.newInstance(Driver.driver());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new IllegalStateException("No se pudo crear la página " + pageType.getSimpleName(), e);
        }
    }

    public static void limpiarCachePaginas() {
        Map<Class<?>, Object> cache = PAGE_CACHE.get();
        cache.clear();
        PAGE_CACHE.remove();
    }

    private static Properties loadDefaults() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("config/defaults.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar config/defaults.properties", e);
        }
        return props;
    }
}
