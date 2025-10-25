package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

public class TransferenciaStepDefinitions {
    @And("accede a la sección de transferencias mediante el enlace \\/\\/a[@id={string}]")
    public void accedeALaSeccionDeTransferenciasMedianteElEnlaceAIdTransferLink() {
    }

    @When("selecciona la cuenta origen {string} en el selector \\/\\/select[@id={string}]")
    public void seleccionaLaCuentaOrigenEnElSelectorSelectIdFromAccount(String arg0) {
    }

    @And("selecciona la cuenta destino {string} en el selector \\/\\/select[@id={string}]")
    public void seleccionaLaCuentaDestinoEnElSelectorSelectIdToAccount(String arg0) {
    }

    @And("ingresa {string} en el campo de monto \\/\\/input[@id={string}]")
    public void ingresaEnElCampoDeMontoInputIdTransferAmount(String arg0, String arg1) {
    }

    @And("confirma la transferencia con el botón \\/\\/input[@id={string} and @name={string} and @type={string}]")
    public void confirmaLaTransferenciaConElBotonInputIdTransferAndNameTransferAndTypeSubmit() {
    }
}
