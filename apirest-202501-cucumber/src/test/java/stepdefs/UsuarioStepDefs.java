package stepdefs;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class UsuarioStepDefs {

    private Response response;

    // ===== COMMON SETUP =====

    @Given("la API está disponible")
    public void apiIsAvailable() {
        RestAssured.baseURI = System.getProperty(
                "api.base",
                "http://localhost:9090/CRUD/api/v1"
        );
    }

    // ===== GENERIC GET =====

    @When("realizo una petición GET a {string}")
    public void sendGETRequestTo(String path) {
        response = given()
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    // ===== GENERIC POST =====

    @When("realizo una petición POST a {string} con body:")
    public void sendPOSTRequestToWithBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    // ===== GENERIC PUT =====

    @When("realizo una petición PUT a {string} con body:")
    public void sendPUTRequestToWithBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }

    // ===== GENERIC PATCH =====

    @When("realizo una petición PATCH a {string} con body:")
    public void sendPATCHRequestToWithBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch(path)
                .then()
                .extract()
                .response();
    }

    // ===== GENERIC DELETE =====

    @When("realizo una petición DELETE a {string}")
    public void sendDELETERequestTo(String path) {
        response = given()
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
    }

    // ===== COMMON ASSERTIONS =====

    @Then("la respuesta debe tener código {int}")
    public void responseShouldHaveStatusCode(int statusCode) {
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @And("la respuesta debe contener información del usuario")
    public void responseShouldContainUserInfo() {
        response.then().body("nombre", notNullValue());
    }

    @And("la respuesta debe contener el mensaje {string}")
    public void responseShouldContainMessage(String message) {
        response.then().body("mensaje", containsString(message));
    }

    @And("la respuesta no debe contener contenido")
    public void responseShouldHaveNoContent() {
        String body = response.getBody().asString();
        assertThat(body)
                .as("Response body should be empty when there is no content")
                .isEmpty();
    }

    // ===== USER CREATION =====

    @And("la respuesta contiene la ubicación del recurso")
    public void responseContainsResourceLocation() {
        String location = response.getHeader("Location");
        assertThat(location)
                .as("Location header should exist and not be blank")
                .isNotBlank();
    }

    @And("la respuesta debe contener el usuario creado")
    public void responseShouldContainCreatedUser() {
        response.then()
                .body("id", notNullValue())
                .body("nombre", notNullValue())
                .body("email", notNullValue())
                .body("fechaCreacion", notNullValue())
                .body("estadoCuenta", notNullValue());
        // fechaActualizacion can be null on creation
    }

    // ===== USER UPDATE =====

    @And("la respuesta debe contener el usuario actualizado con nombre {string} y email {string}")
    public void responseShouldContainUpdatedUser(String expectedName, String expectedEmail) {
        response.then()
                .body("id", notNullValue())
                .body("nombre", equalTo(expectedName))
                .body("email", equalTo(expectedEmail))
                .body("fechaActualizacion", notNullValue());
    }

    // ===== PARTIAL UPDATE (PATCH) =====

    @And("la respuesta debe contener el usuario parcialmente actualizado con campo {string} valor {string}")
    public void responseShouldContainPartiallyUpdatedField(String field, String value) {
        response.then()
                .body(field, equalTo(value))
                .body("id", notNullValue());
    }

    // ===== PAGINATED LIST =====

    @And("la respuesta debe contener una lista de usuarios")
    public void responseShouldContainUserList() {
        List<?> users = response.jsonPath().getList("usuarios");
        assertThat(users)
                .as("The 'usuarios' list should exist")
                .isNotNull();

        int currentPage = response.jsonPath().getInt("paginaActual");
        long totalUsers = response.jsonPath().getLong("totalUsuarios");
        int totalPages = response.jsonPath().getInt("totalPaginas");

        assertThat(currentPage)
                .as("Current page must be >= 1")
                .isGreaterThanOrEqualTo(1);
        assertThat(totalUsers)
                .as("Total users must be >= 0")
                .isGreaterThanOrEqualTo(0);
        assertThat(totalPages)
                .as("Total pages must be >= 0")
                .isGreaterThanOrEqualTo(0);
    }

    // ===== DTO VALIDATION ERRORS =====

    @And("la respuesta debe contener errores de validación para {string}")
    public void responseShouldContainValidationErrorsForField(String field) {
        Object raw = response.jsonPath().get("errores");
        assertThat(raw)
                .as("Response does not contain 'errores'. Body: " + response.getBody().asString())
                .isNotNull();

        List<Map<String, Object>> errors = response.jsonPath().getList("errores");
        assertThat(errors)
                .as("There should be at least one violation. Body: " + response.getBody().asString())
                .isNotEmpty();

        boolean found = errors.stream()
                .anyMatch(e -> field.equalsIgnoreCase(String.valueOf(e.get("campo"))));

        assertThat(found)
                .as("Must contain a violation for field '%s'. Body: %s", field, response.getBody().asString())
                .isTrue();
    }

    // ===== USER PRECONDITIONS =====


    @Given("elimino al usuario con email {string} si existe")
    public void deleteUserByEmailIfExists(String email) {
        Response findResponse = given()
                .when()
                .get("/usuarios/email/" + email)
                .then()
                .extract()
                .response();

        if (findResponse.getStatusCode() == 200) {
            Object idObj = findResponse.jsonPath().get("id");
            if (idObj != null) {
                Long id = Long.valueOf(String.valueOf(idObj));
                given()
                        .when()
                        .delete("/usuarios/" + id)
                        .then()
                        .extract()
                        .response();
            }
        }
    }

    @When("elimino al usuario con email {string}")
    public void elimino_al_usuario_con_email(String email) {
        // Buscar usuario por email para obtener su ID
        Response findResponse = given()
                .when()
                .get("/usuarios/email/" + email)
                .then()
                .extract()
                .response();

        assertThat(findResponse.getStatusCode())
                .as("El usuario debe existir para poder eliminarlo")
                .isEqualTo(200);

        Number idNumber = findResponse.jsonPath().get("id");
        Long id = idNumber.longValue(); // Conversión segura

        // Eliminar usuario por ID
        response = given()
                .when()
                .delete("/usuarios/" + id)
                .then()
                .extract()
                .response();
    }

    @Given("existe un usuario con email {string} para eliminarlo")
    public void ensureUserExistsWithEmailToDelete(String email) {
        Response findResponse = given()
                .when()
                .get("/usuarios/email/" + email)
                .then()
                .extract()
                .response();

        if (findResponse.getStatusCode() != 200) {
            String newUserJson = """
            {
              "nombre": "Usuario a Eliminar",
              "cedula": "99999999",
              "email": "%s",
              "ocupacion": "PROFESOR",
              "clave": "Contrasena123"
            }
            """.formatted(email);

            Response createResponse = given()
                    .contentType(ContentType.JSON)
                    .body(newUserJson)
                    .when()
                    .post("/usuarios")
                    .then()
                    .extract()
                    .response();

            assertThat(createResponse.getStatusCode())
                    .as("User should be created successfully")
                    .isIn(200, 201);
        }
    }
}












