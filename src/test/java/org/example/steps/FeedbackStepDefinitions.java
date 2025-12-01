package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.FeedbackPage;
import org.example.utils.ScenarioDataRepository;
import org.example.utils.TestContext;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedbackStepDefinitions {

    private final TestContext context;
    private Map<String, String> feedbackData;

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

    @When("completa el formulario de feedback con los datos {string}")
    public void completaElFormularioDeFeedbackConLosDatos(String caso) {
        feedbackData = ScenarioDataRepository.getFeedbackData(caso);
        feedbackPage().fillForm(value("nombre"), value("email"), value("asunto"), value("mensaje"));
    }

    @And("envía el formulario de feedback")
    public void enviaElFormularioDeFeedback() {
        feedbackPage().submit();
    }

    @Then("el mensaje de confirmación coincide con los datos configurados")
    public void elMensajeDeConfirmacionCoincide() {
        ensureFeedbackData();
        String mensaje = feedbackPage().getResponseMessage();
        String esperado = value("mensajeesperado");
        assertTrue(mensaje.toLowerCase().contains(esperado.toLowerCase()),
                () -> "El mensaje mostrado no contiene lo esperado: " + esperado);
    }

    private void ensureFeedbackData() {
        if (feedbackData == null) {
            throw new IllegalStateException("No se cargaron los datos de feedback antes de la validación");
        }
    }

    private String value(String key) {
        if (feedbackData == null) {
            return "";
        }
        return feedbackData.getOrDefault(key.toLowerCase(Locale.ROOT), "");
    }
}
