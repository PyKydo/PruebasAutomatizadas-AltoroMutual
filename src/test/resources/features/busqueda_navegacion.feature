@Busqueda
Feature: Búsqueda y navegación de contenido
  El usuario puede buscar información y navegar por el contenido del sitio para encontrar fácilmente lo que necesita.

  Background:
    Given el usuario está en la página principal en la URL "https://demo.testfire.net/"

  Scenario: Búsqueda de información
    When utiliza la función de búsqueda en el campo //input[@id='query' and @name='query' and @type='text'] e ingresa el término "contact"
    And pulsa el botón de búsqueda //input[@type='submit' and @value='Go']
    Then se muestran los resultados en la página con el título //h1[normalize-space(text())='Search Results']

  Scenario: Navegación por secciones
    When navega a la sección "PERSONAL" usando el enlace //a[@id='LinkHeader2' and @class='focus' and normalize-space(text())='PERSONAL']
    Then accede correctamente a la sección seleccionada visible en //h1[translate(normalize-space(text()),'personal','PERSONAL')='PERSONAL']
