package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.pages.SolicitudTarjetaPage;
import org.example.utils.ConfigLoader;
import org.example.utils.ScenarioDataRepository;
import org.example.utils.TestContext;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolicitudTarjetaStepDefinitions {

    private static final Map<String, String> RESULT_MESSAGES = Map.of(
            "aprobado", "Your new Altoro Mutual Gold VISA",
            "rechazado", "Login Failed: We're sorry"
    );

    private final TestContext context;
    private Map<String, String> solicitudData;

    public SolicitudTarjetaStepDefinitions(TestContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return context.getPage(LoginPage.class);
    }

    private SolicitudTarjetaPage solicitudPage() {
        return context.getPage(SolicitudTarjetaPage.class);
    }

    @Given("el usuario autenticado accede a la solicitud de tarjeta")
    public void elUsuarioAutenticadoAccedeALaSolicitudDeTarjeta() {
        LoginPage login = loginPage();
        login.openLoginPage();
        login.performLogin(ConfigLoader.get("app.username"), ConfigLoader.get("app.password"));
        solicitudPage().openApplicationForm();
    }

    @When("ingresa la contraseña configurada {string}")
    public void ingresaLaContrasenaConfigurada(String caso) {
        solicitudData = ScenarioDataRepository.getSolicitudTarjetaData(caso);
        solicitudPage().typeApplicationPassword(value("clave"));
    }

    @And("envía la solicitud de tarjeta")
    public void enviaLaSolicitudDeTarjeta() {
        solicitudPage().submitApplication();
    }

    @Then("el mensaje de solicitud coincide con los datos configurados")
    public void elMensajeDeSolicitudCoincide() {
        ensureSolicitudData();
        String tipoResultado = value("resultado");
        String esperado = RESULT_MESSAGES.get(tipoResultado.toLowerCase());
        if (esperado == null) {
            throw new IllegalArgumentException("Resultado no soportado: " + tipoResultado);
        }
        String mensaje = solicitudPage().getApplicationMessage();
        assertTrue(mensaje.contains(esperado),
                () -> "El mensaje obtenido no coincide con el tipo esperado: " + tipoResultado);
    }

    private void ensureSolicitudData() {
        if (solicitudData == null) {
            throw new IllegalStateException("Los datos de solicitud no han sido inicializados");
        }
    }

    private String value(String key) {
        if (solicitudData == null) {
            return "";
        }
        return solicitudData.getOrDefault(key.toLowerCase(Locale.ROOT), "");
    }
}
