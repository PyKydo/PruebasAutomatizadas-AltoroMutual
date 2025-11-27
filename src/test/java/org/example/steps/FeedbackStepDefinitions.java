package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.FeedbackPage;
import org.example.utils.TestContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedbackStepDefinitions {

    private final TestContext context;

    public FeedbackStepDefinitions(TestContext context) {
        this.context = context;
    }

    private FeedbackPage feedbackPage() {
        return context.getPage(FeedbackPage.class);
    }

    @Given("el usuario abre el formulario de feedback")
    public void elUsuarioAbreElFormularioDeFeedback() {
        feedbackPage().openPage();
    }

    @When("completa el formulario de feedback con nombre {string}, email {string}, asunto {string} y mensaje {string}")
    public void completaElFormularioDeFeedback(String nombre, String email, String asunto, String mensaje) {
        feedbackPage().fillForm(nombre, email, asunto, mensaje);
    }

    @And("envía el formulario de feedback")
    public void enviaElFormularioDeFeedback() {
        feedbackPage().submit();
    }

    @Then("el mensaje de confirmación contiene {string}")
    public void elMensajeDeConfirmacionContiene(String mensajeEsperado) {
        String mensaje = feedbackPage().getResponseMessage();
        assertTrue(mensaje.toLowerCase().contains(mensajeEsperado.toLowerCase()),
                () -> "El mensaje mostrado no contiene lo esperado: " + mensajeEsperado);
    }
}
