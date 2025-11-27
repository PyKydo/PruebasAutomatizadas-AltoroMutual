package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Centraliza la lectura de propiedades ubicadas en src/test/resources/config.
 */
public final class ConfigLoader {

    private static final String CONFIG_PATH = "config/test.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("No se encontró el archivo de configuración: " + CONFIG_PATH);
            }
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Error cargando configuraciones: " + e.getMessage());
        }
    }

    private ConfigLoader() {
    }

    public static String get(String key) {
        return Objects.requireNonNull(PROPERTIES.getProperty(key),
                () -> "Propiedad no definida: " + key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
}
