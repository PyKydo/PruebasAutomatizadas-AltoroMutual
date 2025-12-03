package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.BusquedaNavegacionPage;
import org.example.utils.Config;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusquedaNavegacionStepDefinitions {

    private BusquedaNavegacionPage page() {
        return Config.pagina(BusquedaNavegacionPage.class);
    }

    @Given("el usuario accede al portal principal de Altoro Mutual")
    public void elUsuarioAccedeAlPortalPrincipal() {
        page().abrirHome();
    }

    @When("el usuario busca el término {string}")
    public void elUsuarioBuscaElTermino(String termino) {
        page().buscarTermino(termino);
    }

    @When("el usuario navega a la sección {string}")
    public void elUsuarioNavegaALaSeccion(String seccion) {
        page().irASeccion(seccion);
    }

    @Then("el encabezado principal contiene {string}")
    public void elEncabezadoPrincipalContiene(String textoEsperado) {
        String heading = page().obtenerTituloPrincipal();
        assertTrue(heading.contains(textoEsperado),
                () -> "El encabezado no contiene el texto esperado: " + textoEsperado);
    }
}
