package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SolicitudTarjetaPage extends BasePage {

    private static final By APPLY_LINK = By.xpath("//a[@href='apply.jsp']");
    private static final By PASSWORD_INPUT = By.name("passwd");
    private static final By SUBMIT_BUTTON = By.name("Submit");
    private static final By ERROR_MESSAGE = By.id("_ctl0__ctl0_Content_Main_message");
    private static final By SUCCESS_MESSAGE = By.id("_ctl0__ctl0_Content_Main_lblMessage");

    public SolicitudTarjetaPage(WebDriver driver) {
        super(driver);
    }

    public void abrirFormularioSolicitud() {
        hacerClick(APPLY_LINK);
    }

    public void ingresarClaveSolicitud(String password) {
        ingresarTexto(PASSWORD_INPUT, password == null ? "" : password);
    }

    public void enviarSolicitud() {
        hacerClick(SUBMIT_BUTTON);
    }

    public String obtenerMensajeSolicitud() {
        if (estaElementoPresente(SUCCESS_MESSAGE)) {
            return obtenerTexto(SUCCESS_MESSAGE);
        }
        return obtenerTexto(ERROR_MESSAGE);
    }
}
