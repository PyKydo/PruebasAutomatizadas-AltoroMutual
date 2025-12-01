@SolicitudTarjeta
Feature: Solicitud de la tarjeta Altoro Mutual Gold Visa
  Un cliente autenticado puede solicitar la tarjeta Gold Visa confirmando su contraseña.

  Background:
    Given el usuario autenticado accede a la solicitud de tarjeta

  Scenario Outline: Resultado de la solicitud de tarjeta
    When ingresa la contraseña configurada "<caso_solicitud>"
    And envía la solicitud de tarjeta
    Then el mensaje de solicitud coincide con los datos configurados

    Examples:
      | caso_solicitud             |
      | solicitud_tarjeta_aprobada |
      | solicitud_tarjeta_rechazada |
