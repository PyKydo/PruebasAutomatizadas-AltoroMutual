package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.utils.ScenarioDataRepository;
import org.example.utils.TestContext;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStepDefinitions {

    private final TestContext context;
    private Map<String, String> loginData;

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

    @When("inicia sesión con los datos {string}")
    public void iniciaSesionConLosDatos(String caso) {
        loginData = ScenarioDataRepository.getLoginData(caso);
        loginPage().performLogin(value(loginData, "usuario"), value(loginData, "contrasena"));
    }

    @Then("el resultado del login coincide con los datos configurados")
    public void elResultadoDelLoginCoincide() {
        ensureLoginData();
        boolean esperadoExitoso = "exitoso".equalsIgnoreCase(value(loginData, "resultado"));
        if (esperadoExitoso) {
            assertTrue(loginPage().isUserLoggedIn(), "Se esperaba un login exitoso");
        } else {
            assertTrue(loginPage().isErrorMessageVisible(), "Se esperaba un mensaje de error");
        }
    }

    private void ensureLoginData() {
        if (loginData == null) {
            throw new IllegalStateException("Los datos de login no fueron cargados en el paso When");
        }
    }

    private String value(Map<String, String> data, String key) {
        if (data == null) {
            return "";
        }
        return data.getOrDefault(key.toLowerCase(Locale.ROOT), "");
    }
}
