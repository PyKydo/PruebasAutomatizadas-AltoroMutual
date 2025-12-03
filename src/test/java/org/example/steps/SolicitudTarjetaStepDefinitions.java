package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LoginPage;
import org.example.pages.SolicitudTarjetaPage;
import org.example.utils.ExcelUtils;
import org.example.utils.Config;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolicitudTarjetaStepDefinitions {

    private static final Map<String, String> RESULT_MESSAGES = Map.of(
            "aprobado", "Your new Altoro Mutual Gold VISA",
            "rechazado", "Login Failed: We're sorry"
    );

    private Map<String, String> solicitudData;

    private LoginPage loginPage() {
        return Config.pagina(LoginPage.class);
    }

    private SolicitudTarjetaPage solicitudPage() {
        return Config.pagina(SolicitudTarjetaPage.class);
    }

    @Given("el usuario autenticado accede a la solicitud de tarjeta")
    public void elUsuarioAutenticadoAccedeALaSolicitudDeTarjeta() {
        LoginPage login = loginPage();
        login.abrirPortalLogin();
        login.ejecutarLogin(Config.config("app.username"),
            Config.config("app.password"));
        solicitudPage().abrirFormularioSolicitud();
    }

    @When("ingresa la contraseña configurada {string}")
    public void ingresaLaContrasenaConfigurada(String caso) {
        solicitudData = ExcelUtils.datosSolicitud(caso);
        solicitudPage().ingresarClaveSolicitud(value("password", "clave", "contrasena", "contraseña"));
    }

    @And("envía la solicitud de tarjeta")
    public void enviaLaSolicitudDeTarjeta() {
        solicitudPage().enviarSolicitud();
    }

    @Then("el mensaje de solicitud coincide con los datos configurados")
    public void elMensajeDeSolicitudCoincide() {
        ensureSolicitudData();
        String tipoResultado = value("resultado");
        String esperado = RESULT_MESSAGES.get(tipoResultado.toLowerCase());
        if (esperado == null) {
            throw new IllegalArgumentException("Resultado no soportado: " + tipoResultado);
        }
        String mensaje = solicitudPage().obtenerMensajeSolicitud();
        assertTrue(mensaje.contains(esperado),
                () -> "El mensaje obtenido no coincide con el tipo esperado: " + tipoResultado);
    }

    private void ensureSolicitudData() {
        if (solicitudData == null) {
            throw new IllegalStateException("Los datos de solicitud no han sido inicializados");
        }
    }

    private String value(String... keys) {
        if (solicitudData == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            String normalized = key.toLowerCase(Locale.ROOT);
            String value = solicitudData.get(normalized);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return "";
    }
}
