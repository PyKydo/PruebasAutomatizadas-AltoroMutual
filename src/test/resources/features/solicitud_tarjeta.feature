@SolicitudTarjeta
Feature: Solicitud de la tarjeta Altoro Mutual Gold Visa
  Un cliente autenticado puede solicitar la tarjeta Gold Visa confirmando su contraseña.

  Background:
    Given el usuario inicia sesión para la solicitud de tarjeta
    And navega a la sección de solicitud mediante el enlace "//a[@href='apply.jsp']"

  Scenario Outline: Resultado de la solicitud de tarjeta
    When ingresa la contraseña de solicitud "<clave>" en el campo "//input[@name='passwd']"
    And envía la solicitud con el botón "//input[@name='Submit' and @type='submit']"
    Then se muestra el mensaje de solicitud en "<xpath_mensaje>" con "<mensaje_esperado>"

    Examples:
      | clave                 | xpath_mensaje                                      | mensaje_esperado                                                                 |
      | demo1234             | //span[@id='_ctl0__ctl0_Content_Main_lblMessage']  | Your new Altoro Mutual Gold VISA with a $10000 and 7.9% APR will be sent in the mail. |
      | contraseña_incorrecta | //span[@id='_ctl0__ctl0_Content_Main_message']    | Login Failed: We're sorry, but this username or password was not found in our system. Please try again. |
