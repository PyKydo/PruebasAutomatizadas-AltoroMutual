package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.ConsultaSaldoPage;
import org.example.pages.LoginPage;
import org.example.utils.ExcelUtils;
import org.example.utils.Config;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultaSaldoStepDefinitions {

    private Map<String, String> consultaData;

    private LoginPage loginPage() {
        return Config.pagina(LoginPage.class);
    }

    private ConsultaSaldoPage consultaSaldoPage() {
        return Config.pagina(ConsultaSaldoPage.class);
    }

    @Given("el usuario inicia sesión para consultar saldos")
    public void elUsuarioIniciaSesionParaConsultarSaldos() {
        loginPage().abrirPortalLogin();
        loginPage().ejecutarLogin(Config.config("app.username"),
            Config.config("app.password"));
    }

    @When("selecciona la cuenta configurada {string} desde el panel principal")
    public void seleccionaLaCuentaConfigurada(String caso) {
        consultaData = ExcelUtils.datosConsultaSaldo(caso);
        consultaSaldoPage().seleccionarCuenta(value("cuenta"));
    }

    @And("confirma la consulta del historial de la cuenta")
    public void confirmaLaConsultaDelHistorialDeLaCuenta() {
        consultaSaldoPage().solicitarHistorial();
    }

    @Then("el encabezado de historial coincide con los datos configurados")
    public void elEncabezadoDeHistorialCoincide() {
        ensureConsultaData();
        String titulo = consultaSaldoPage().obtenerTituloHistorial();
        assertTrue(titulo.contains(value("cuenta")),
                "El encabezado no contiene el identificador de cuenta esperado.");
    }

    @And("el detalle de balance presenta un monto disponible")
    public void elDetalleDeBalancePresentaUnMontoDisponible() {
        String balance = consultaSaldoPage().obtenerSaldoFinal();
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
