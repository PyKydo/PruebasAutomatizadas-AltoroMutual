@Login
Feature: Autenticación de usuario en el sistema
  El usuario puede iniciar sesión para acceder a las funcionalidades protegidas del sitio.

  Background:
    Given el usuario abre la aplicación de Altoro Mutual

  Scenario Outline: Inicio de sesión con diferentes credenciales
    When inicia sesión con los datos "<caso_login>"
    Then el resultado del login coincide con los datos configurados

    Examples:
      | caso_login                         |
      | inicio_sesion_jsmith_exitoso       |
      | inicio_sesion_admin_exitoso        |
      | inicio_sesion_campos_vacios        |
      | inicio_sesion_contrasena_incorrecta |