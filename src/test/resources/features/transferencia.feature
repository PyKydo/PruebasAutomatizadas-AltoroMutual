@Transferencia
Feature: Transferencia de fondos entre cuentas
  El usuario autenticado puede transferir fondos entre sus cuentas para gestionar su dinero fácilmente.

  Background:
    Given el usuario ha iniciado sesión
    And accede a la sección de transferencias mediante el enlace "//a[@id='MenuHyperLink3']"

  Scenario Outline: Transferencia de fondos entre diferentes cuentas
    When selecciona la cuenta origen "<cuenta_origen>" en el selector "//select[@id='fromAccount']"
    And selecciona la cuenta destino "<cuenta_destino>" en el selector "//select[@id='toAccount']"
    And ingresa "<monto>" en el campo de monto "//input[@id='transferAmount']"
    And confirma la transferencia con el botón "//input[@id='transfer']"
    Then el mensaje en "//span[@id='_ctl0__ctl0_Content_Main_postResp']" contiene "<mensaje_esperado>"

    Examples:
      | cuenta_origen | cuenta_destino | monto | mensaje_esperado                                 |
      | 800002        | 800003         | 100   | was successfully transferred from Account 800002 |

  Scenario: Evita transferencia con la misma cuenta origen y destino
    When selecciona la cuenta origen "800002" en el selector "//select[@id='fromAccount']"
    And selecciona la cuenta destino "800002" en el selector "//select[@id='toAccount']"
    And ingresa "100" en el campo de monto "//input[@id='transferAmount']"
    And confirma la transferencia con el botón "//input[@id='transfer']"
    Then se muestra una alerta con el mensaje "From Account and To Account fields cannot be the same."