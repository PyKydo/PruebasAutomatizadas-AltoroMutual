package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.ConsultaSaldoPage;
import org.example.pages.LoginPage;
import org.example.utils.ConfigLoader;
import org.example.utils.ScenarioDataRepository;
import org.example.utils.TestContext;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultaSaldoStepDefinitions {

    private final TestContext context;
    private Map<String, String> consultaData;

    public ConsultaSaldoStepDefinitions(TestContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return context.getPage(LoginPage.class);
    }

    private ConsultaSaldoPage consultaSaldoPage() {
        return context.getPage(ConsultaSaldoPage.class);
    }

    @Given("el usuario inicia sesión para consultar saldos")
    public void elUsuarioIniciaSesionParaConsultarSaldos() {
        loginPage().openLoginPage();
        loginPage().performLogin(ConfigLoader.get("app.username"), ConfigLoader.get("app.password"));
    }

    @When("selecciona la cuenta configurada {string} desde el panel principal")
    public void seleccionaLaCuentaConfigurada(String caso) {
        consultaData = ScenarioDataRepository.getConsultaSaldoData(caso);
        consultaSaldoPage().selectAccount(value("cuenta"));
    }

    @And("confirma la consulta del historial de la cuenta")
    public void confirmaLaConsultaDelHistorialDeLaCuenta() {
        consultaSaldoPage().requestHistory();
    }

    @Then("el encabezado de historial coincide con los datos configurados")
    public void elEncabezadoDeHistorialCoincide() {
        ensureConsultaData();
        String titulo = consultaSaldoPage().getHistoryTitle();
        assertTrue(titulo.contains(value("cuenta")),
                "El encabezado no contiene el identificador de cuenta esperado.");
    }

    @And("el detalle de balance presenta un monto disponible")
    public void elDetalleDeBalancePresentaUnMontoDisponible() {
        String balance = consultaSaldoPage().getBalanceValue();
        assertFalse(balance.trim().isEmpty(), "El monto de balance no debería estar vacío.");
    }

    private void ensureConsultaData() {
        if (consultaData == null) {
            throw new IllegalStateException("Los datos de consulta no fueron inicializados");
        }
    }

    private String value(String key) {
        if (consultaData == null) {
            return "";
        }
        return consultaData.getOrDefault(key.toLowerCase(Locale.ROOT), "");
    }
}
