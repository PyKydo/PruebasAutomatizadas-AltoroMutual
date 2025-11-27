@SolicitudTarjeta
Feature: Solicitud de la tarjeta Altoro Mutual Gold Visa
  Un cliente autenticado puede solicitar la tarjeta Gold Visa confirmando su contraseña.

  Background:
    Given el usuario autenticado accede a la solicitud de tarjeta

  Scenario Outline: Resultado de la solicitud de tarjeta
    When ingresa la contraseña de solicitud "<clave>"
    And envía la solicitud de tarjeta
    Then el mensaje de solicitud indica "<resultado>"

    Examples:
      | clave                 | resultado  |
      | demo1234             | aprobado   |
      | contraseña_incorrecta | rechazado |
