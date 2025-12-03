package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.FeedbackPage;
import org.example.utils.ExcelUtils;
import org.example.utils.Config;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedbackStepDefinitions {

    private Map<String, String> feedbackData;

    private FeedbackPage feedbackPage() {
        return Config.pagina(FeedbackPage.class);
    }

    @Given("el usuario abre el formulario de feedback")
    public void elUsuarioAbreElFormularioDeFeedback() {
        feedbackPage().abrirFeedback();
    }

    @When("completa el formulario de feedback con los datos {string}")
    public void completaElFormularioDeFeedbackConLosDatos(String caso) {
        feedbackData = ExcelUtils.datosFeedback(caso);
        feedbackPage().completarFormulario(value("nombre"), value("email"), value("asunto"), value("mensaje"));
    }

    @And("envía el formulario de feedback")
    public void enviaElFormularioDeFeedback() {
        feedbackPage().enviarFormulario();
    }

    @Then("el mensaje de confirmación coincide con los datos configurados")
    public void elMensajeDeConfirmacionCoincide() {
        ensureFeedbackData();
        String mensaje = feedbackPage().obtenerMensajeRespuesta();
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
