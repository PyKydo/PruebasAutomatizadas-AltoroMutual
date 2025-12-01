@Feedback
Feature: Envío de feedback al banco
  El usuario puede enviar comentarios o sugerencias para comunicarse con el banco.

  Background:
    Given el usuario abre el formulario de feedback

  Scenario Outline: Verificación del formulario de envío de feedback
    When completa el formulario de feedback con los datos "<caso_feedback>"
    And envía el formulario de feedback
    Then el mensaje de confirmación coincide con los datos configurados

    Examples:
      | caso_feedback              |
      | feedback_consulta_valido   |
      | feedback_email_invalido    |