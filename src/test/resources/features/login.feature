@Login
Feature: Autenticación de usuario en el sistema
  El usuario puede iniciar sesión para acceder a las funcionalidades protegidas del sitio.

  Background:
    Given el usuario está en la página de login en la URL "https://demo.testfire.net/login.jsp"

  Scenario Outline: Inicio de sesión
    When completa el campo usuario "//input[@id='uid']" con "<usuario>"
    And completa el campo contraseña "//input[@id='passw']" con "<contraseña>"
    And pulsa el botón de login "//input[@name='btnSubmit' and @type='submit']"
    Then se muestra un mensaje en "<xpath_mensaje>"

    Examples:
      | usuario      | contraseña  | xpath_mensaje                                  |
      | jsmith       | demo1234    | //a[normalize-space()='Sign Off']              |
      | admin        | admin       | //a[normalize-space()='Sign Off']              |
      | usuariofalso | contrafalsa | //span[@id='_ctl0__ctl0_Content_Main_message'] |
      |              |             | //span[@id='_ctl0__ctl0_Content_Main_message'] |
      | jsmith       | incorrecta  | //span[@id='_ctl0__ctl0_Content_Main_message'] |