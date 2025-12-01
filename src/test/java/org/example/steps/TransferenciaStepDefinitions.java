package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.pages.TransferPage;
import org.example.utils.ConfigLoader;
import org.example.utils.ScenarioDataRepository;
import org.example.utils.TestContext;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferenciaStepDefinitions {

    private final TestContext context;
    private Map<String, String> transferenciaData;

    public TransferenciaStepDefinitions(TestContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return context.getPage(LoginPage.class);
    }

    private TransferPage transferPage() {
        return context.getPage(TransferPage.class);
    }

    @Given("el usuario autenticado accede a transferencias con los datos {string}")
    public void elUsuarioAutenticadoAccedeATransferenciasConDatos(String dataId) {
        transferenciaData = loadTransferData(dataId);
        String username = fallback("usuario", ConfigLoader.get("app.username"));
        String password = fallback("password", ConfigLoader.get("app.password"));
        loginPage().openLoginPage();
        loginPage().performLogin(username, password);
        transferPage().openTransferSection();
    }

    @When("realiza la transferencia configurada")
    public void realizaTransferenciaConfigurada() {
        ensureDataLoaded();
        transferPage().selectFromAccount(required("cuentaorigen"));
        transferPage().selectToAccount(required("cuentadestino"));
        transferPage().setAmount(required("monto"));
        transferPage().submitTransfer();
    }

    @Then("el mensaje de transferencia coincide con los datos configurados")
    public void elMensajeDeTransferenciaCoincideConLosDatosConfigurados() {
        ensureDataLoaded();
        String mensajeEsperado = required("mensajeesperado");
        if (mensajeEsperado == null || mensajeEsperado.isBlank()) {
            throw new IllegalStateException("El mensaje esperado no está definido para el caso actual");
        }
        assertTrue(transferPage().getResponseMessage().contains(mensajeEsperado),
                () -> "El mensaje no contiene: " + mensajeEsperado);
    }

    @Then("la alerta de transferencia coincide con los datos configurados")
    public void laAlertaDeTransferenciaCoincideConLosDatosConfigurados() {
        ensureDataLoaded();
        String expected = required("mensajeesperado");
        if (expected == null || expected.isBlank()) {
            throw new IllegalStateException("El mensaje esperado de alerta no está configurado en el Excel");
        }
        String actual = transferPage().acceptAlertText();
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
        return ScenarioDataRepository.getTransferenciaData(id);
    }
}
