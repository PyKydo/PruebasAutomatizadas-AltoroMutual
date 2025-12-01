@ConsultaSaldo
Feature: Consulta de saldos y movimientos de cuentas
  Un usuario autenticado puede revisar el historial y balance de sus cuentas desde el sitio.

  Background:
    Given el usuario inicia sesión para consultar saldos

  Scenario Outline: Visualización del historial y balance de la cuenta
    When selecciona la cuenta configurada "<caso_consulta>" desde el panel principal
    And confirma la consulta del historial de la cuenta
    Then el encabezado de historial coincide con los datos configurados
    And el detalle de balance presenta un monto disponible

    Examples:
      | caso_consulta          |
      | consulta_cuenta_ahorro |
      | consulta_cuenta_inversion |
