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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ScenarioDataRepository {

    private static final String DEFAULT_EXCEL = ConfigLoader.getOrDefault(
            "scenario.excel.path", "testData/data.xlsx");
    private static final String TRANSFER_EXCEL = ConfigLoader.getOrDefault(
            "transfer.excel.path", DEFAULT_EXCEL);
    private static final String TRANSFER_SHEET = ConfigLoader.getOrDefault(
            "transfer.excel.sheet", "DatosUsuarios");
    private static final DataFormatter FORMATTER = new DataFormatter(Locale.ROOT);

    private ScenarioDataRepository() {
    }

    public static Map<String, String> getLoginData(String id) {
        return readRow("LoginDatos", id);
    }

    public static Map<String, String> getConsultaSaldoData(String id) {
        return readRow("ConsultaSaldo", id);
    }

    public static Map<String, String> getFeedbackData(String id) {
        return readRow("Feedback", id);
    }

    public static Map<String, String> getSolicitudTarjetaData(String id) {
        return readRow("SolicitudTarjeta", id);
    }

    public static Map<String, String> getTransferenciaData(String id) {
        return readRow(TRANSFER_EXCEL, TRANSFER_SHEET, id);
    }

    private static Map<String, String> readRow(String sheetName, String dataId) {
        return readRow(DEFAULT_EXCEL, sheetName, dataId);
    }

    private static Map<String, String> readRow(String excelPath, String sheetName, String dataId) {
        if (dataId == null || dataId.isBlank()) {
            throw new IllegalArgumentException("El identificador de datos no puede ser nulo");
        }
        try (InputStream stream = openExcel(excelPath); XSSFWorkbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalStateException(
                        "No se encontró la hoja " + sheetName + " en el Excel " + excelPath);
            }
            Row header = sheet.getRow(0);
            if (header == null) {
                throw new IllegalStateException(
                        "La hoja " + sheetName + " no tiene encabezados en la primera fila");
            }
            Map<String, Integer> columns = buildColumnIndex(header);
            Integer idIndex = columns.get("dataid");
            if (idIndex == null) {
                throw new IllegalStateException(
                        "La hoja " + sheetName + " debe tener una columna 'dataId'");
            }
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String currentId = readCell(row, idIndex);
                if (dataId.equalsIgnoreCase(currentId)) {
                    return toValueMap(sheetName, dataId, columns, row);
                }
            }
            throw new IllegalArgumentException(
                    "No se encontró el id '" + dataId + "' en la hoja " + sheetName);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo el archivo Excel " + excelPath, e);
        }
    }

    private static Map<String, String> toValueMap(String sheet, String dataId,
                                                  Map<String, Integer> columns, Row row) {
        Map<String, String> values = new LinkedHashMap<>();
        columns.forEach((name, index) -> values.put(name, readCell(row, index)));
        values.putIfAbsent("dataid", dataId);
        return values;
    }

    private static Map<String, Integer> buildColumnIndex(Row header) {
        Map<String, Integer> index = new HashMap<>();
        header.forEach(cell -> {
            String name = FORMATTER.formatCellValue(cell).trim();
            if (!name.isBlank()) {
                index.put(name.toLowerCase(Locale.ROOT), cell.getColumnIndex());
            }
        });
        return index;
    }

    private static String readCell(Row row, int cellIndex) {
        if (row.getCell(cellIndex) == null) {
            return "";
        }
        return FORMATTER.formatCellValue(row.getCell(cellIndex)).trim();
    }

    private static InputStream openExcel(String resourcePath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream classpathStream = classLoader.getResourceAsStream(resourcePath);
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
