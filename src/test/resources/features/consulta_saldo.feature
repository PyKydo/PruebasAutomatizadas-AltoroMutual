@ConsultaSaldo
Feature: Consulta de saldos y movimientos de cuentas
  Un usuario autenticado puede revisar el historial y balance de sus cuentas desde el sitio.

  Background:
    Given el usuario inicia sesión para consultar saldos

  Scenario Outline: Visualización del historial y balance de la cuenta
    When selecciona la cuenta "<cuenta>" desde el panel principal
    And confirma la consulta del historial de la cuenta
    Then el encabezado de historial incluye "<cuenta>"
    And el detalle de balance presenta un monto disponible

    Examples:
      | cuenta |
      | 800002 |
      | 800003 |
