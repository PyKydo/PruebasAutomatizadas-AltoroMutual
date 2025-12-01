@Transferencia
Feature: Transferencia de fondos entre cuentas
  El usuario autenticado puede transferir fondos entre sus cuentas para gestionar su dinero fácilmente.

  Scenario Outline: Transferencia de fondos con estrategia híbrida
    Given el usuario autenticado accede a transferencias con los datos "<data_id>"
    When realiza la transferencia configurada
    Then el mensaje de transferencia coincide con los datos configurados

    Examples:
      | data_id                                   |
      | transferencia_exitosa_principal           |
      | transferencia_exitosa_tarjeta_origen      |
      | transferencia_exitosa_destino_alternativo |

  Scenario Outline: Validaciones de alertas con fuentes mixtas
    Given el usuario autenticado accede a transferencias con los datos "<data_id>"
    When realiza la transferencia configurada
    Then la alerta de transferencia coincide con los datos configurados

    Examples:
      | data_id                                |
      | transferencia_alerta_mismas_cuentas    |
      | transferencia_alerta_monto_invalido    |