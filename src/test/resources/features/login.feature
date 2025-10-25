@Login
Feature: Autenticación de usuario en el sistema
  El usuario puede iniciar sesión para acceder a las funcionalidades protegidas del sitio.

  Background:
    Given el usuario está en la página de login en la URL "https://demo.testfire.net/login.jsp"

  Scenario Outline: Inicio de sesión
    When completa el campo usuario "//input[@id='uid']" con "<usuario>"
    And completa el campo contraseña "//input[@id='passw']" con "<contraseña>"
    And pulsa el botón de login "//input[@name='btnSubmit' and @type='submit']"
    Then se muestra el mensaje de <tipo_mensaje> en "<xpath_mensaje>"

    Examples:
      | usuario   | contraseña   | tipo_mensaje     | xpath_mensaje                                                                       |
      | jsmith    | demo1234     | éxito            | //a[contains(text(),'Sign Off')]                                                    |
      | admin     | admin        | éxito            | //a[contains(text(),'Sign Off')]                                                    |
      | fakeuser  | fakepass     | error            | //span[@id='_ctl0__ctl0_Content_Main_message']                                      |
      |           |              | error            | //span[@id='_ctl0__ctl0_Content_Main_message']                                      |
      | jsmith    | incorrecta   | error            | //span[@id='_ctl0__ctl0_Content_Main_message']                                      |