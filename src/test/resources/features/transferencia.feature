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
    Then el mensaje de transferencia en "//span[@id='_ctl0__ctl0_Content_Main_postResp']" contiene "<mensaje_esperado>"

    Examples:
      | cuenta_origen    | cuenta_destino | monto | mensaje_esperado                                          |
      | 800002           | 800003         | 100   | was successfully transferred from Account 800002          |
      | 4539082039396288 | 800003         | 50    | was successfully transferred from Account 4539082039396288 |

  Scenario Outline: Validaciones de alerta durante la transferencia
    When selecciona la cuenta origen "<cuenta_origen>" en el selector "//select[@id='fromAccount']"
    And selecciona la cuenta destino "<cuenta_destino>" en el selector "//select[@id='toAccount']"
    And ingresa "<monto>" en el campo de monto "//input[@id='transferAmount']"
    And confirma la transferencia con el botón "//input[@id='transfer']"
    Then se muestra una alerta de transferencia con el mensaje "<mensaje_alerta>"

    Examples:
      | cuenta_origen | cuenta_destino | monto | mensaje_alerta                                            |
      | 800002        | 800002         | 100   | From Account and To Account fields cannot be the same.    |
      | 800002        | 800003         | 0     | Transfer Amount must be a number greater than 0.          |