@Balance
Feature: Consulta de balances de cuentas
  El usuario autenticado puede consultar el balance de sus cuentas para conocer el estado de sus finanzas.

  Background:
    Given el usuario ha iniciado sesión
    And accede a la sección de balances mediante el enlace //a[@id='balances-link']

  Scenario: Consulta de balance exitosa
    When selecciona la cuenta "800002" en el selector //select[@id='listAccounts']
    And pulsa el botón de seleccionar cuenta //input[@id='btnGetAccount']
    Then se muestra el balance actual de la cuenta en //td[@align='right'][1]
