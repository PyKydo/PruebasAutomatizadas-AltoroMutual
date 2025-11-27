package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.pages.TransferPage;
import org.example.utils.ConfigLoader;
import org.example.utils.JsonDataProvider;
import org.example.utils.TestContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferenciaStepDefinitions {

    private final TestContext context;

    public TransferenciaStepDefinitions(TestContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return context.getPage(LoginPage.class);
    }

    private TransferPage transferPage() {
        return context.getPage(TransferPage.class);
    }

    @Given("el usuario autenticado accede a transferencias")
    public void elUsuarioAutenticadoAccedeATransferencias() {
        loginPage().openLoginPage();
        loginPage().performLogin(ConfigLoader.get("app.username"), ConfigLoader.get("app.password"));
        transferPage().openTransferSection();
    }

    @When("realiza una transferencia desde {string} hacia {string} por {string}")
    public void realizaUnaTransferenciaDesdeHaciaPor(String cuentaOrigen, String cuentaDestino, String monto) {
        transferPage().selectFromAccount(cuentaOrigen);
        transferPage().selectToAccount(cuentaDestino);
        transferPage().setAmount(monto);
        transferPage().submitTransfer();
    }

    @Then("el mensaje de transferencia contiene {string}")
    public void elMensajeDeTransferenciaContiene(String mensajeEsperado) {
        assertTrue(transferPage().getResponseMessage().contains(mensajeEsperado),
                () -> "El mensaje no contiene: " + mensajeEsperado);
    }

    @Then("la alerta de transferencia muestra {string}")
    public void laAlertaDeTransferenciaMuestra(String alertaKey) {
        String expected = JsonDataProvider.getValue("transfer-alerts.json", alertaKey);
        String actual = transferPage().acceptAlertText();
        assertTrue(actual.contains(expected),
                () -> "Se esperaba alerta: " + expected + " pero se obtuvo: " + actual);
    }
}
