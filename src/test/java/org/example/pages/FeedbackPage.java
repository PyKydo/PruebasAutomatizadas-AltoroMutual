package org.example.pages;

import org.example.utils.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FeedbackPage extends BasePage {

    private static final By NAME_INPUT = By.name("name");
    private static final By EMAIL_INPUT = By.name("email_addr");
    private static final By SUBJECT_INPUT = By.name("subject");
    private static final By MESSAGE_TEXTAREA = By.name("comments");
    private static final By SUBMIT_BUTTON = By.xpath("//input[@name='submit' and @type='submit']");
    private static final By RESPONSE_PARAGRAPH = By.xpath("//body/table/tbody/tr[2]/td[2]/div/p");

    public FeedbackPage(WebDriver driver) {
        super(driver);
    }

    public void abrirFeedback() {
        navegarConReintentos(Config.config("app.baseUrl") + "/feedback.jsp");
    }

    public void completarFormulario(String nombre, String email, String asunto, String mensaje) {
        ingresarTexto(NAME_INPUT, nombre);
        ingresarTexto(EMAIL_INPUT, email);
        ingresarTexto(SUBJECT_INPUT, asunto);
        ingresarTexto(MESSAGE_TEXTAREA, mensaje);
    }

    public void enviarFormulario() {
        hacerClick(SUBMIT_BUTTON);
    }

    public String obtenerMensajeRespuesta() {
        return obtenerTexto(RESPONSE_PARAGRAPH);
    }
}
