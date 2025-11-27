@Transferencia
Feature: Transferencia de fondos entre cuentas
  El usuario autenticado puede transferir fondos entre sus cuentas para gestionar su dinero fácilmente.

  Background:
    Given el usuario autenticado accede a transferencias

  Scenario Outline: Transferencia de fondos entre diferentes cuentas
    When realiza una transferencia desde "<cuenta_origen>" hacia "<cuenta_destino>" por "<monto>"
    Then el mensaje de transferencia contiene "<mensaje_esperado>"

    Examples:
      | cuenta_origen    | cuenta_destino | monto | mensaje_esperado                                          |
      | 800002           | 800003         | 100   | was successfully transferred from Account 800002          |
      | 4539082039396288 | 800003         | 50    | was successfully transferred from Account 4539082039396288 |

  Scenario Outline: Validaciones de alerta durante la transferencia
    When realiza una transferencia desde "<cuenta_origen>" hacia "<cuenta_destino>" por "<monto>"
    Then la alerta de transferencia muestra "<clave_alerta>"

    Examples:
      | cuenta_origen | cuenta_destino | monto | clave_alerta     |
      | 800002        | 800002         | 100   | sameAccount      |
      | 800002        | 800003         | 0     | invalidAmount    |