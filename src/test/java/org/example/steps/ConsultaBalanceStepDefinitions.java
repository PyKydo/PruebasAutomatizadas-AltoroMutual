package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConsultaBalanceStepDefinitions {
    @Given("el usuario ha iniciado sesión")
    public void elUsuarioHaIniciadoSesion() {
    }

    @And("accede a la sección de balances mediante el enlace \\/\\/a[@id={string}]")
    public void accedeALaSeccionDeBalancesMedianteElEnlaceAIdBalancesLink() {
    }

    @When("selecciona la cuenta {string} en el selector \\/\\/select[@id={string}]")
    public void seleccionaLaCuentaEnElSelectorSelectIdListAccounts(String arg0) {
    }

    @And("pulsa el botón de seleccionar cuenta \\/\\/input[@id={string}]")
    public void pulsaElBotonDeSeleccionarCuentaInputIdBtnGetAccount() {
    }

    @Then("se muestra el balance actual de la cuenta en \\/\\/td[@align={string}][{int}]")
    public void seMuestraElBalanceActualDeLaCuentaEnTdAlignRight(int arg0) {
    }
}
