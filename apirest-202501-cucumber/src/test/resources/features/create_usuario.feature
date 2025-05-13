Feature: Crear usuario

  Background:
    Given la API está disponible

  Scenario: Crear usuario con datos válidos
    Given elimino al usuario con email "juan.pera@example.com" si existe
    When realizo una petición POST a "/usuarios" con body:
    """
    {
      "nombre": "Juan Perezz",
      "cedula": "123456688",
      "email": "juan.pera@example.com",
      "ocupacion": "ESTUDIANTE",
      "clave": "Abcdef12e"
    }
    """
    Then la respuesta debe tener código 201
    And la respuesta contiene la ubicación del recurso
    And la respuesta debe contener el usuario creado


  Scenario: Crear usuario sin nombre
    Given elimino al usuario con email "juan.perez@example.com" si existe
    When realizo una petición POST a "/usuarios" con body:
      """
      {
        "nombre": "",
        "cedula": "1234567",
        "email": "juan.perez@example.com",
        "ocupacion": "ESTUDIANTE",
        "clave": "Abcdef12"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "nombre"

  Scenario: Crear usuario con email inválido
    When realizo una petición POST a "/usuarios" con body:
      """
      {
        "nombre": "Juan Perez",
        "cedula": "1234567",
        "email": "juan.invalid-email",
        "ocupacion": "PROFESOR",
        "clave": "Abcdef12"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "email"

  Scenario: Crear usuario con ocupación no permitida
    When realizo una petición POST a "/usuarios" con body:
      """
      {
        "nombre": "Ana Gomez",
        "cedula": "7654321",
        "email": "ana.gomez@example.com",
        "ocupacion": "ADMIN",
        "clave": "XyZ12345"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "ocupacion"

  Scenario: Crear usuario con clave que no cumple patrón
    When realizo una petición POST a "/usuarios" con body:
      """
      {
        "nombre": "Luis Rojas",
        "cedula": "1122334",
        "email": "luis.rojas@example.com",
        "ocupacion": "ESTUDIANTE",
        "clave": "password"
      }
      """
    Then la respuesta debe tener código 400
    And la respuesta debe contener errores de validación para "clave"

