package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class ConsultaSaldoPage extends BasePage {

    private static final By ACCOUNT_LIST = By.id("listAccounts");
    private static final By GET_ACCOUNT_BUTTON = By.id("btnGetAccount");
    private static final By HISTORY_TITLE = By.tagName("h1");
    private static final By BALANCE_CELL = By.xpath("//td[contains(text(),'Ending balance')]/following-sibling::td");

    public ConsultaSaldoPage(WebDriver driver) {
        super(driver);
    }

    public void seleccionarCuenta(String value) {
        new Select(esperarVisibilidad(ACCOUNT_LIST)).selectByValue(value);
    }

    public void solicitarHistorial() {
        hacerClick(GET_ACCOUNT_BUTTON);
    }

    public String obtenerTituloHistorial() {
        return obtenerTexto(HISTORY_TITLE);
    }

    public String obtenerSaldoFinal() {
        return obtenerTexto(BALANCE_CELL);
    }
}
