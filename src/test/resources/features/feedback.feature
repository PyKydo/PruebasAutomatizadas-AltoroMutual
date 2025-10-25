@Feedback
Feature: Envío de feedback al banco
  El usuario puede enviar comentarios o sugerencias para comunicarse con el banco.

  Background:
    Given el usuario está en la página de feedback en la URL "https://demo.testfire.net/feedback.jsp"

  Scenario Outline: Verificación del formulario de envío de feedback
    When completa el campo nombre "//input[@name='name']" con "<nombre>"
    And completa el campo email "//input[@name='email_addr']" con "<email>"
    And completa el campo asunto "//input[@name='subject']" con "<asunto>"
    And completa el campo mensaje "//textarea[@name='comments']" con "<mensaje>"
    And envía el formulario con el botón "//input[@name='submit' and @type='submit']"
  Then el mensaje de feedback en "//body/table/tbody/tr[2]/td[2]/div/p" contiene "<mensaje_esperado>"

    Examples:
      | nombre     | email                | asunto   | mensaje                             | mensaje_esperado                                    |
      | Juan Perez | juan.perez@gmail.com | Consulta | Tengo una consulta sobre mi cuenta. | Thank you for your comments, Juan Perez.            |
      | Juan Perez | juan.perez@          | Consulta | Mensaje con email inválido.         | However, the email you gave is incorrect            |