Feature: Actualización parcial de usuario

  Background:
    Given la API está disponible

  Scenario: Actualizar solo el nombre de un usuario existente
    When realizo una petición PATCH a "/usuarios/24" con body:
      """
      {
        "nombre": "Carlos Parcial"
      }
      """
    Then la respuesta debe tener código 200
    And la respuesta debe contener el usuario parcialmente actualizado con campo "nombre" valor "Carlos Parcial"

  Scenario: userId no numérico en PATCH
    When realizo una petición PATCH a "/usuarios/abc" con body:
      """
      {
        "email": "nuevo.email@uni.edu"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener el mensaje "Parámetro 'userId' con valor 'abc' no es un número válido"

  Scenario: usuario inexistente en PATCH
    When realizo una petición PATCH a "/usuarios/9999" con body:
      """
      {
        "nombre": "No Existe"
      }
      """
    Then la respuesta debe tener código 404
    And la respuesta debe contener el mensaje "Usuario no encontrado"

  Scenario: validación parcial (nombre vacío)
    When realizo una petición PATCH a "/usuarios/17" con body:
      """
      {
        "nombre": ""
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "nombre"
