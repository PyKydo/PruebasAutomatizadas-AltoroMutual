package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Utilidad simple para obtener valores desde archivos JSON en src/test/resources/testData.
 */
public final class JsonDataProvider {

    private static final String BASE_PATH = "testData/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataProvider() {
    }

    public static JsonNode read(String relativePath) {
        String resourcePath = BASE_PATH + relativePath;
        try (InputStream stream = JsonDataProvider.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("No se encontró el recurso JSON: " + resourcePath);
            }
            return MAPPER.readTree(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo JSON " + resourcePath, e);
        }
    }

    public static String getValue(String relativePath, String dotPath) {
        JsonNode node = read(relativePath);
        JsonNode current = node;
        for (String key : Arrays.asList(dotPath.split("\\."))) {
            current = current.get(key);
            if (current == null) {
                throw new IllegalArgumentException("Ruta " + dotPath + " no encontrada en " + relativePath);
            }
        }
        return current.asText();
    }
}
