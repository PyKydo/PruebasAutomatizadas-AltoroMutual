@Feedback
Feature: Envío de feedback al banco
  El usuario puede enviar comentarios o sugerencias para comunicarse con el banco.

  Background:
    Given el usuario está en la página de feedback en la URL "https://demo.testfire.net/feedback.jsp"

  Scenario Outline: Verificación del formulario de envío de feedback
    When completa el campo nombre //input[@name='name'] con "<nombre>"
    And completa el campo email //input[@name='email_addr'] con "<email>"
    And completa el campo asunto //input[@name='subject'] con "<asunto>"
    And completa el campo mensaje //textarea[@name='comments'] con "<mensaje>"
    And envía el formulario con el botón //input[@name='submit' and @type='submit']
    Then se muestra el mensaje esperado en el elemento con XPath "<xpath_mensaje>"

      Examples:
        | caso_de_prueba          | nombre       | email                      | asunto     | mensaje                                   | xpath_mensaje                                           |
        | Envío exitoso           | Juan Pérez   | juan.perez@gmail.com       | Consulta   | "Tengo una consulta sobre mi cuenta."     | //body[contains(text(),'Thank you for your comments')]  |
        | Mensaje vacío           | Camila Rojas | camila.rojas@outlook.cl    | Reclamo    | ""                                        | //body[contains(text(),'Please provide a comment')]     |
        | Email inválido          | Juan Pérez   | juan.perez@                | Consulta   | "Email inválido."                         | //body[contains(text(),'Please enter a valid email')]   |
        | Asunto vacío            | Juan Pérez   | juan.perez@gmail.com       | ""         | "Sin asunto."                             | //body[contains(text(),'Please enter a subject')]       |
        | Todos los campos vacíos | ""           | ""                         | ""         | ""                                        | //body[contains(text(),'Please fill all fields')]       |