@Login
Feature: Autenticación de usuario en el sistema
  El usuario puede iniciar sesión para acceder a las funcionalidades protegidas del sitio.

  Background:
    Given el usuario abre la aplicación de Altoro Mutual

  Scenario Outline: Inicio de sesión con diferentes credenciales
    When inicia sesión con usuario "<usuario>" y contraseña "<contraseña>"
    Then el resultado del login es "<resultado>"

    Examples:
      | usuario | contraseña | resultado |
      | jsmith  | demo1234   | exitoso   |
      | admin   | admin      | exitoso   |
      |         |            | fallido   |
      | jsmith  | incorrecta | fallido   |