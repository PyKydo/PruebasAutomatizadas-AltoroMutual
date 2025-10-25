@SolicitudTarjeta
Feature: Solicitud de la tarjeta Altoro Mutual Gold Visa
  Un cliente autenticado puede solicitar la tarjeta Gold Visa confirmando su contraseña.

  Background:
    Given el usuario inicia sesión para la solicitud de tarjeta
    And navega a la sección de solicitud mediante el enlace "//a[@href='apply.jsp']"

  Scenario: Solicitud aprobada con la contraseña correcta
    When ingresa la contraseña de solicitud "demo1234" en el campo "//input[@name='passwd']"
    And envía la solicitud con el botón "//input[@name='Submit' and @type='submit']"
    Then se muestra el mensaje de aprobación en "//span[@id='_ctl0__ctl0_Content_Main_lblMessage']" con "Your new Altoro Mutual Gold VISA with a $10000 and 7.9% APR will be sent in the mail."

  Scenario: Solicitud rechazada con la contraseña incorrecta
    When ingresa la contraseña de solicitud "contraseña_incorrecta" en el campo "//input[@name='passwd']"
    And envía la solicitud con el botón "//input[@name='Submit' and @type='submit']"
    Then se muestra el mensaje de error de solicitud en "//span[@id='_ctl0__ctl0_Content_Main_message']" con "Login Failed: We're sorry, but this username or password was not found in our system. Please try again."
