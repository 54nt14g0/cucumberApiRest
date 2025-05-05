Feature: Obtener usuario por ID

  Scenario: Obtener usuario existente por ID válido
    Given la API está disponible
    When realizo una petición GET a "/usuarios/1"
    Then la respuesta debe tener código 200
    And la respuesta debe contener información del usuario

  Scenario: Obtener usuario con ID no numérico
    Given la API está disponible
    When realizo una petición GET a "/usuarios/abc"
    Then la respuesta debe tener código 400
    And la respuesta debe contener el mensaje "Parámetro 'userId' con valor 'abc' no es un número válido"

  Scenario: Obtener usuario inexistente por ID
    Given la API está disponible
    When realizo una petición GET a "/usuarios/999999"
    Then la respuesta debe tener código 404
    And la respuesta debe contener el mensaje "Usuario no encontrado"

