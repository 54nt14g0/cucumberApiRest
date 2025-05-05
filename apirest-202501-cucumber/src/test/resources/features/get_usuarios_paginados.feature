Feature: Obtener lista paginada de usuarios

  Scenario: Obtener lista de usuarios con parámetros válidos
    Given la API está disponible
    When realizo una petición GET a "/usuarios?page=1&size=10"
    Then la respuesta debe tener código 200
    And la respuesta debe contener una lista de usuarios

  Scenario: Parámetros no numéricos
    Given la API está disponible
    When realizo una petición GET a "/usuarios?page=abc&size=10"
    Then la respuesta debe tener código 400

  Scenario: Parámetros menores que 1
    Given la API está disponible
    When realizo una petición GET a "/usuarios?page=0&size=0"
    Then la respuesta debe tener código 400

