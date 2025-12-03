package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;

import java.time.Duration;

public class TransferenciaPage extends BasePage {

    private static final By TRANSFER_LINK = By.id("MenuHyperLink3");
    private static final By FROM_ACCOUNT_SELECT = By.id("fromAccount");
    private static final By TO_ACCOUNT_SELECT = By.id("toAccount");
    private static final By AMOUNT_INPUT = By.id("transferAmount");
    private static final By SUBMIT_BUTTON = By.id("transfer");
    private static final By RESPONSE_MESSAGE = By.id("_ctl0__ctl0_Content_Main_postResp");

    public TransferenciaPage(WebDriver driver) {
        super(driver);
    }

    public void abrirModuloTransferencias() {
        hacerClick(TRANSFER_LINK);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(FROM_ACCOUNT_SELECT));
    }

    public void seleccionarCuentaOrigen(String value) {
        new Select(esperarVisibilidad(FROM_ACCOUNT_SELECT)).selectByValue(value);
    }

    public void seleccionarCuentaDestino(String value) {
        new Select(esperarVisibilidad(TO_ACCOUNT_SELECT)).selectByValue(value);
    }

    public void definirMonto(String amount) {
        ingresarTexto(AMOUNT_INPUT, amount);
    }

    public void enviarTransferencia() {
        hacerClick(SUBMIT_BUTTON);
    }

    public String obtenerMensajeTransferencia() {
        return obtenerTexto(RESPONSE_MESSAGE);
    }

    public String aceptarAlertaYObtenerTexto() {
        Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }
}
