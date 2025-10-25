package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeedbackStepDefinitions {
    @Given("el usuario está en la página de feedback en la URL {string}")
    public void elUsuarioEstaEnLaPaginaDeFeedbackEnLaURL(String arg0) {
    }

    @When("completa el campo nombre \\/\\/input[@name={string}] con {string}")
    public void completaElCampoNombreInputNameNameCon(String arg0, String arg1) {
    }

    @And("completa el campo email \\/\\/input[@name={string}] con {string}")
    public void completaElCampoEmailInputNameEmail_addrCon(String arg0, String arg1) {
    }

    @And("completa el campo asunto \\/\\/input[@name={string}] con {string}")
    public void completaElCampoAsuntoInputNameSubjectCon(String arg0, String arg1) {
    }

    @And("completa el campo mensaje \\/\\/textarea[@name={string}] con {string}")
    public void completaElCampoMensajeTextareaNameCommentsCon(String arg0, String arg1) {
    }

    @And("envía el formulario con el botón \\/\\/input[@name={string} and @type={string}]")
    public void enviaElFormularioConElBotonInputNameSubmitAndTypeSubmit() {
    }

    @Then("se muestra el mensaje esperado en el elemento con XPath {string}")
    public void seMuestraElMensajeEsperadoEnElElementoConXPath(String arg0, String arg1) {
    }
}
