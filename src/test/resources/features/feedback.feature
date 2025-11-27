@Feedback
Feature: Envío de feedback al banco
  El usuario puede enviar comentarios o sugerencias para comunicarse con el banco.

  Background:
    Given el usuario abre el formulario de feedback

  Scenario Outline: Verificación del formulario de envío de feedback
    When completa el formulario de feedback con nombre "<nombre>", email "<email>", asunto "<asunto>" y mensaje "<mensaje>"
    And envía el formulario de feedback
    Then el mensaje de confirmación contiene "<mensaje_esperado>"

    Examples:
      | nombre     | email                | asunto   | mensaje                             | mensaje_esperado                                    |
      | Juan Perez | juan.perez@gmail.com | Consulta | Tengo una consulta sobre mi cuenta. | Thank you for your comments, Juan Perez.            |
      | Juan Perez | juan.perez@          | Consulta | Mensaje con email inválido.         | However, the email you gave is incorrect            |