@Transferencia
Feature: Transferencia de fondos entre cuentas
  El usuario autenticado puede transferir fondos entre sus cuentas para gestionar su dinero fácilmente.

  Background:
    Given el usuario ha iniciado sesión
    And accede a la sección de transferencias mediante el enlace //a[@id='transfer-link']

  Scenario Outline: Transferencia de fondos
    When selecciona la cuenta origen "800002" en el selector //select[@id='fromAccount']
    And selecciona la cuenta destino "800003" en el selector //select[@id='toAccount']
    And ingresa "<monto>" en el campo de monto //input[@id='transferAmount']
    And confirma la transferencia con el botón //input[@id='transfer' and @name='transfer' and @type='submit']
    Then se muestra el mensaje de <tipo_mensaje> en <xpath_mensaje>

    Examples:
      | monto    | tipo_mensaje                        | xpath_mensaje                                                                                     |
      | 100      | éxito                               | //span[@id='_ctl0__ctl0_Content_Main_postResp']                                                   |
      | -100     | error de monto negativo             | //span[@id='_ctl0__ctl0_Content_Main_postResp' and contains(.,'must be a number greater than 0')] |
      | 0        | error de monto cero                 | //span[@id='_ctl0__ctl0_Content_Main_postResp' and contains(.,'must be a number greater than 0')] |
      |          | error de campo vacío                | //span[@id='_ctl0__ctl0_Content_Main_postResp' and contains(.,'must be a number greater than 0')] |
      | misma    | error de misma cuenta               | //span[@id='_ctl0__ctl0_Content_Main_postResp' and contains(.,'cannot be the same')]              |

