package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.pages.TransferenciaPage;
import org.example.utils.ExcelUtils;
import org.example.utils.Config;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferenciaStepDefinitions {

    private Map<String, String> transferenciaData;

    private LoginPage loginPage() {
        return Config.pagina(LoginPage.class);
    }

    private TransferenciaPage transferPage() {
        return Config.pagina(TransferenciaPage.class);
    }

    @Given("el usuario autenticado accede a transferencias con los datos {string}")
    public void elUsuarioAutenticadoAccedeATransferenciasConDatos(String dataId) {
        transferenciaData = loadTransferData(dataId);
        String username = fallback("usuario", Config.config("app.username"));
        String password = fallback("password", Config.config("app.password"));
        loginPage().abrirPortalLogin();
        loginPage().ejecutarLogin(username, password);
        transferPage().abrirModuloTransferencias();
    }

    @When("realiza la transferencia configurada")
    public void realizaTransferenciaConfigurada() {
        ensureDataLoaded();
        transferPage().seleccionarCuentaOrigen(required("cuentaorigen"));
        transferPage().seleccionarCuentaDestino(required("cuentadestino"));
        transferPage().definirMonto(required("monto"));
        transferPage().enviarTransferencia();
    }

    @Then("el mensaje de transferencia coincide con los datos configurados")
    public void elMensajeDeTransferenciaCoincideConLosDatosConfigurados() {
        ensureDataLoaded();
        String mensajeEsperado = required("mensajeesperado");
        if (mensajeEsperado == null || mensajeEsperado.isBlank()) {
            throw new IllegalStateException("El mensaje esperado no está definido para el caso actual");
        }
        assertTrue(transferPage().obtenerMensajeTransferencia().contains(mensajeEsperado),
                () -> "El mensaje no contiene: " + mensajeEsperado);
    }

    @Then("la alerta de transferencia coincide con los datos configurados")
    public void laAlertaDeTransferenciaCoincideConLosDatosConfigurados() {
        ensureDataLoaded();
        String expected = required("mensajeesperado");
        if (expected == null || expected.isBlank()) {
            throw new IllegalStateException("El mensaje esperado de alerta no está configurado en el Excel");
        }
        String actual = transferPage().aceptarAlertaYObtenerTexto();
        assertTrue(actual.contains(expected),
                () -> "Se esperaba alerta: " + expected + " pero se obtuvo: " + actual);
    }

    private void ensureDataLoaded() {
        if (transferenciaData == null) {
            throw new IllegalStateException("Los datos de transferencia no han sido inicializados en el Given");
        }
    }

    private String fallback(String key, String defaultValue) {
        if (transferenciaData == null) {
            return defaultValue;
        }
        String value = transferenciaData.getOrDefault(key.toLowerCase(Locale.ROOT), "");
        return value.isBlank() ? defaultValue : value;
    }

    private String required(String key) {
        if (transferenciaData == null) {
            throw new IllegalStateException("Los datos de transferencia no han sido inicializados");
        }
        String value = transferenciaData.getOrDefault(key.toLowerCase(Locale.ROOT), "");
        if (value.isBlank()) {
            throw new IllegalStateException("El campo '" + key + "' no está configurado en el Excel para este dataset");
        }
        return value;
    }

    private Map<String, String> loadTransferData(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador de datos no puede ser nulo");
        }
        return ExcelUtils.datosTransferencia(id);
    }
}
