package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.ConsultaSaldoPage;
import org.example.pages.LoginPage;
import org.example.utils.ConfigLoader;
import org.example.utils.TestContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultaSaldoStepDefinitions {

    private final TestContext context;

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

    @When("selecciona la cuenta {string} desde el panel principal")
    public void seleccionaLaCuentaDesdeElPanelPrincipal(String cuenta) {
        consultaSaldoPage().selectAccount(cuenta);
    }

    @And("confirma la consulta del historial de la cuenta")
    public void confirmaLaConsultaDelHistorialDeLaCuenta() {
        consultaSaldoPage().requestHistory();
    }

    @Then("el encabezado de historial incluye {string}")
    public void elEncabezadoDeHistorialIncluye(String cuenta) {
        String titulo = consultaSaldoPage().getHistoryTitle();
        assertTrue(titulo.contains(cuenta), "El encabezado no contiene el identificador de cuenta esperado.");
    }

    @And("el detalle de balance presenta un monto disponible")
    public void elDetalleDeBalancePresentaUnMontoDisponible() {
        String balance = consultaSaldoPage().getBalanceValue();
        assertFalse(balance.trim().isEmpty(), "El monto de balance no debería estar vacío.");
    }
}
