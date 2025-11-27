package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.BusquedaNavegacionPage;
import org.example.utils.TestContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusquedaNavegacionStepDefinitions {

    private final TestContext context;

    public BusquedaNavegacionStepDefinitions(TestContext context) {
        this.context = context;
    }

    private BusquedaNavegacionPage page() {
        return context.getPage(BusquedaNavegacionPage.class);
    }

    @Given("el usuario accede al portal principal de Altoro Mutual")
    public void elUsuarioAccedeAlPortalPrincipal() {
        page().openHomePage();
    }

    @When("el usuario busca el término {string}")
    public void elUsuarioBuscaElTermino(String termino) {
        page().searchFor(termino);
    }

    @When("el usuario navega a la sección {string}")
    public void elUsuarioNavegaALaSeccion(String seccion) {
        page().navigateToSection(seccion);
    }

    @Then("el encabezado principal contiene {string}")
    public void elEncabezadoPrincipalContiene(String textoEsperado) {
        String heading = page().getMainHeadingText();
        assertTrue(heading.contains(textoEsperado),
                () -> "El encabezado no contiene el texto esperado: " + textoEsperado);
    }
}
