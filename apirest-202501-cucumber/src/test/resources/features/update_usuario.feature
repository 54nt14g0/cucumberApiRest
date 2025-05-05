Feature: Actualizar usuario existente

  Background:
    Given la API está disponible

  Scenario: Actualizar usuario con datos válidos
    When realizo una petición PUT a "/usuarios/20" con body:
      """
      {
        "nombre": "Carlos M. Mendoza",
        "cedula": "9876543",
        "email": "carlos.mendoza@uni.edu",
        "ocupacion": "PROFESOR",
        "clave": "NewPass12"
      }
      """
    Then la respuesta debe tener código 200
    And la respuesta debe contener el usuario actualizado con nombre "Carlos M. Mendoza" y email "carlos.mendoza@uni.edu"

  Scenario: Actualizar usuario con userId no numérico
    When realizo una petición PUT a "/usuarios/abc" con body:
      """
      {
        "nombre": "Nombre",
        "cedula": "12345",
        "email": "test@uni.edu",
        "ocupacion": "ESTUDIANTE",
        "clave": "Abcde123"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener el mensaje "Parámetro 'userId' con valor 'abc' no es un número válido"

  Scenario: Actualizar usuario inexistente
    When realizo una petición PUT a "/usuarios/9999" con body:
      """
      {
        "nombre": "Nombre",
        "cedula": "12345",
        "email": "test@uni.edu",
        "ocupacion": "ESTUDIANTE",
        "clave": "Abcde123"
      }
      """
    Then la respuesta debe tener código 404
    And la respuesta debe contener el mensaje "Usuario no encontrado"

  Scenario: Error de validación al actualizar (nombre vacío)
    When realizo una petición PUT a "/usuarios/17" con body:
      """
      {
        "nombre": "",
        "cedula": "9876543",
        "email": "carlos.mendoza@uni.edu",
        "ocupacion": "PROFESOR",
        "clave": "NewPass12"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "nombre"
