package org.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.utils.ExcelUtils;
import org.example.utils.Config;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStepDefinitions {

    private Map<String, String> loginData;

    private LoginPage loginPage() {
        return Config.pagina(LoginPage.class);
    }

    @Given("el usuario abre la aplicación de Altoro Mutual")
    public void elUsuarioAbreLaAplicacion() {
        loginPage().abrirPortalLogin();
    }

    @When("inicia sesión con los datos {string}")
    public void iniciaSesionConLosDatos(String caso) {
        loginData = ExcelUtils.datosLogin(caso);
        loginPage().ejecutarLogin(
            value(loginData, "usuario"),
            value(loginData, "password", "contrasena", "contraseña", "clave")
        );
    }

    @Then("el resultado del login coincide con los datos configurados")
    public void elResultadoDelLoginCoincide() {
        ensureLoginData();
        boolean esperadoExitoso = "exitoso".equalsIgnoreCase(value(loginData, "resultado"));
        if (esperadoExitoso) {
            assertTrue(loginPage().usuarioAutenticado(), "Se esperaba un login exitoso");
        } else {
            assertTrue(loginPage().mensajeErrorVisible(), "Se esperaba un mensaje de error");
        }
    }

    private void ensureLoginData() {
        if (loginData == null) {
            throw new IllegalStateException("Los datos de login no fueron cargados en el paso When");
        }
    }

    private String value(Map<String, String> data, String... keys) {
        if (data == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            String normalized = key.toLowerCase(Locale.ROOT);
            String value = data.get(normalized);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return "";
    }
}
