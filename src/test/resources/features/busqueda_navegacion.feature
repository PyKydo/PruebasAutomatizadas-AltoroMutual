@Busqueda
Feature: Búsqueda y navegación de contenido
  El usuario puede buscar información y navegar por el contenido del sitio para encontrar fácilmente lo que necesita.

  Background:
    Given el usuario accede al portal principal de Altoro Mutual

  Scenario: Búsqueda de información
    When el usuario busca el término "contact"
    Then el encabezado principal contiene "Search Results"

  Scenario: Navegación por secciones
    When el usuario navega a la sección "personal"
    Then el encabezado principal contiene "Personal"

  Scenario: Navegación a la sección de negocios
    When el usuario navega a la sección "small business"
    Then el encabezado principal contiene "Small Business"
