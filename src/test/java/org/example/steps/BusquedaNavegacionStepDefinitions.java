package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BusquedaNavegacionStepDefinitions {
    @Given("el usuario está en la página principal en la URL {string}")
    public void elUsuarioEstaEnLaPaginaPrincipalEnLaURL(String arg0) {
    }

    @When("utiliza la función de búsqueda en el campo \\/\\/input[@id={string} and @name={string} and @type={string}] e ingresa el término {string}")
    public void utilizaLaFuncionDeBusquedaEnElCampoInputIdQueryAndNameQueryAndTypeTextEIngresaElTermino(String arg0) {
    }

    @And("pulsa el botón de búsqueda \\/\\/input[@type={string} and @value={string}]")
    public void pulsaElBotonDeBusquedaInputTypeSubmitAndValueGo() {
    }

    @Then("se muestran los resultados en la página con el título \\/\\/h{int}[normalize-space\\(text\\())={string}]")
    public void seMuestranLosResultadosEnLaPaginaConElTituloHNormalizeSpaceTextSearchResults(int arg0) {
    }

    @When("navega a la sección {string} usando el enlace \\/\\/a[@id={string} and @class={string} and normalize-space\\(text\\())={string}]")
    public void navegaALaSeccionUsandoElEnlaceAIdLinkHeaderAndClassFocusAndNormalizeSpaceTextPERSONAL(String arg0, int arg1) {
    }

    @Then("accede correctamente a la sección seleccionada visible en \\/\\/h{int}[translate\\(normalize-space\\(text\\()),{string},{string})={string}]")
    public void accedeCorrectamenteALaSeccionSeleccionadaVisibleEnHTranslateNormalizeSpaceTextPersonalPERSONALPERSONAL(int arg0) {
    }
}
