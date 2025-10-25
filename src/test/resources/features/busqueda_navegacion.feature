@Busqueda
Feature: Búsqueda y navegación de contenido
  El usuario puede buscar información y navegar por el contenido del sitio para encontrar fácilmente lo que necesita.

  Background:
    Given el usuario está en la página principal en la URL "https://demo.testfire.net/"

  Scenario: Búsqueda de información
    When utiliza la función de búsqueda en el campo "//input[@id='query']" e ingresa el término "contact"
    And pulsa el botón de búsqueda "//input[@type='submit' and @value='Go']"
    Then el título de resultados "//h1[normalize-space()='Search Results']" es visible

  Scenario: Navegación por secciones
    When navega a la sección mediante el enlace "//a[@id='LinkHeader2']"
    Then el encabezado "//h1" contiene "Personal"
