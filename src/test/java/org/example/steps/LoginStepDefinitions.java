package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.utils.TestContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStepDefinitions {

    private final TestContext context;

    public LoginStepDefinitions(TestContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return context.getPage(LoginPage.class);
    }

    @Given("el usuario abre la aplicación de Altoro Mutual")
    public void elUsuarioAbreLaAplicacion() {
        loginPage().openLoginPage();
    }

    @When("inicia sesión con usuario {string} y contraseña {string}")
    public void iniciaSesionConCredenciales(String usuario, String contrasena) {
        loginPage().performLogin(usuario, contrasena);
    }

    @Then("el resultado del login es {string}")
    public void elResultadoDelLoginEs(String estado) {
        boolean esperadoExitoso = "exitoso".equalsIgnoreCase(estado);
        if (esperadoExitoso) {
            assertTrue(loginPage().isUserLoggedIn(), "Se esperaba un login exitoso");
        } else {
            assertTrue(loginPage().isErrorMessageVisible(), "Se esperaba un mensaje de error");
        }
    }
}
