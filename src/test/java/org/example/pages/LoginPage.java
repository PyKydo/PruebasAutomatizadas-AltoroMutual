package org.example.pages;

import org.example.utils.Config;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("uid");
    private static final By PASSWORD_INPUT = By.id("passw");
    private static final By SUBMIT_BUTTON = By.name("btnSubmit");
    private static final By SIGN_OFF_LINK = By.linkText("Sign Off");
    private static final By ERROR_MESSAGE = By.id("_ctl0__ctl0_Content_Main_message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void abrirPortalLogin() {
        String loginUrl = Config.config("app.baseUrl") + "/login.jsp";
        navegarConReintentos(loginUrl);
        descartarAlertaPendiente();
    }

    public void ejecutarLogin(String username, String password) {
        ingresarTexto(USERNAME_INPUT, username == null ? "" : username);
        ingresarTexto(PASSWORD_INPUT, password == null ? "" : password);
        hacerClick(SUBMIT_BUTTON);
        descartarAlertaPendiente();
    }

    public boolean usuarioAutenticado() {
        try {
            return esperarVisibilidad(SIGN_OFF_LINK).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean mensajeErrorVisible() {
        try {
            return esperarVisibilidad(ERROR_MESSAGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void descartarAlertaPendiente() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {
        }
    }
}
