package org.example.utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter(Locale.ROOT);

    private ExcelUtils() {
    }

    public static Map<String, String> datosLogin(String id) {
        return leerHoja(pathEscenarios(), "LoginDatos", id);
    }

    public static Map<String, String> datosConsultaSaldo(String id) {
        return leerHoja(pathEscenarios(), "ConsultaSaldo", id);
    }

    public static Map<String, String> datosFeedback(String id) {
        return leerHoja(pathEscenarios(), "Feedback", id);
    }

    public static Map<String, String> datosSolicitud(String id) {
        return leerHoja(pathEscenarios(), "SolicitudTarjeta", id);
    }

    public static Map<String, String> datosTransferencia(String id) {
        return leerHoja(pathEscenarios(), "Transferencias", id);
    }

    private static String pathEscenarios() {
        return Config.config("scenario.excel.path", "testData/data.xlsx");
    }

    private static Map<String, String> leerHoja(String excelPath, String sheetName, String dataId) {
        if (dataId == null || dataId.isBlank()) {
            throw new IllegalArgumentException("El identificador de datos no puede ser nulo");
        }
        try (InputStream input = abrirExcel(excelPath); XSSFWorkbook book = new XSSFWorkbook(input)) {
            Sheet sheet = book.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalStateException("No se encontró la hoja " + sheetName + " en " + excelPath);
            }
            Row header = sheet.getRow(0);
            if (header == null) {
                throw new IllegalStateException("La hoja " + sheetName + " requiere encabezados");
            }
            Map<String, Integer> columnas = construirIndices(header);
            Integer idIndex = columnas.get("dataid");
            if (idIndex == null) {
                throw new IllegalStateException("La hoja " + sheetName + " debe tener la columna 'dataId'");
            }
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String currentId = leerCelda(row, idIndex);
                if (dataId.equalsIgnoreCase(currentId)) {
                    return construirMapa(columnas, row, dataId);
                }
            }
            throw new IllegalArgumentException("No se encontró el id '" + dataId + "' en " + sheetName);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo el archivo Excel " + excelPath, e);
        }
    }

    private static Map<String, Integer> construirIndices(Row header) {
        Map<String, Integer> index = new LinkedHashMap<>();
        header.forEach(cell -> {
            String name = FORMATTER.formatCellValue(cell).trim();
            if (!name.isBlank()) {
                index.put(name.toLowerCase(Locale.ROOT), cell.getColumnIndex());
            }
        });
        return index;
    }

    private static Map<String, String> construirMapa(Map<String, Integer> columnas, Row row, String dataId) {
        Map<String, String> values = new LinkedHashMap<>();
        columnas.forEach((name, idx) -> values.put(name, leerCelda(row, idx)));
        values.putIfAbsent("dataid", dataId);
        return values;
    }

    private static String leerCelda(Row row, int columnIndex) {
        if (row.getCell(columnIndex) == null) {
            return "";
        }
        return FORMATTER.formatCellValue(row.getCell(columnIndex)).trim();
    }

    private static InputStream abrirExcel(String resourcePath) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream classpathStream = loader.getResourceAsStream(resourcePath);
        if (classpathStream != null) {
            return classpathStream;
        }
        Path fileSystemPath = Paths.get(resourcePath);
        if (Files.exists(fileSystemPath)) {
            return Files.newInputStream(fileSystemPath);
        }
        throw new IllegalStateException("No se pudo localizar el recurso Excel: " + resourcePath);
    }
}
