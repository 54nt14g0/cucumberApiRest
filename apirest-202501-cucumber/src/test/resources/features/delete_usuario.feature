Feature: Eliminación de usuario

  Background:
    Given la API está disponible

  Scenario: Eliminar usuario existente
    Given existe un usuario con email "eliminar@correo.com" para eliminarlo
    When elimino al usuario con email "eliminar@correo.com"
    Then la respuesta debe tener código 204
    And la respuesta no debe contener contenido



  Scenario: Eliminar usuario con userId no numérico
    When realizo una petición DELETE a "/usuarios/abc"
    Then la respuesta debe tener código 400
    And la respuesta debe contener el mensaje "Parámetro 'userId' con valor 'abc' no es un número válido"

  Scenario: Eliminar usuario inexistente
    When realizo una petición DELETE a "/usuarios/9999"
    Then la respuesta debe tener código 404
    And la respuesta debe contener el mensaje "Usuario no encontrado"
