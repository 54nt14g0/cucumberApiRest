package stepdefs;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.http.ContentType;



public class UsuarioStepDefs {

    private Response response;

    // ===== CONFIGURACIÓN COMÚN =====

    @Given("la API está disponible")
    public void laApiEstaDisponible() {
        RestAssured.baseURI = System.getProperty(
                "api.base",
                "http://localhost:9090/CRUD/api/v1"
        );
    }

    // ===== GET GENÉRICO =====

    @When("realizo una petición GET a {string}")
    public void realizoUnaPeticionGETA(String path) {
        response = given()
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    // ===== POST GENÉRICO =====

    @When("realizo una petición POST a {string} con body:")
    public void realizoUnaPeticionPOSTConBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    // ===== ASERCIONES COMUNES =====

    @Then("la respuesta debe tener código {int}")
    public void laRespuestaDebeTenerCodigo(int statusCode) {
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @And("la respuesta debe contener información del usuario")
    public void laRespuestaContenerInfoUsuario() {
        response.then().body("nombre", notNullValue());
    }

    @And("la respuesta debe contener el mensaje {string}")
    public void laRespuestaContenerMensaje(String mensaje) {
        response.then().body("mensaje", containsString(mensaje));
    }

    // ===== CREACIÓN DE USUARIO =====

    @And("la respuesta contiene la ubicación del recurso")
    public void laRespuestaContieneLaUbicacionDelRecurso() {
        String location = response.getHeader("Location");
        assertThat(location)
                .as("La cabecera Location debe existir y no estar vacía")
                .isNotBlank();
    }

    @And("la respuesta debe contener el usuario creado")
    public void laRespuestaDebeContenerElUsuarioCreado() {
        response.then()
                .body("id", notNullValue())
                .body("nombre", notNullValue())
                .body("email", notNullValue())
                .body("fechaCreacion", notNullValue())
                .body("estadoCuenta", notNullValue());
        // No comprobamos fechaActualizacion porque en creación suele ser null
    }

    // ===== LISTA PAGINADA =====

    @And("la respuesta debe contener una lista de usuarios")
    public void laRespuestaContenerListaUsuarios() {
        List<?> usuarios = response.jsonPath().getList("usuarios");
        assertThat(usuarios)
                .as("La lista 'usuarios' debe existir")
                .isNotNull();

        int paginaActual = response.jsonPath().getInt("paginaActual");
        long totalUsuarios = response.jsonPath().getLong("totalUsuarios");
        int totalPaginas = response.jsonPath().getInt("totalPaginas");

        assertThat(paginaActual)
                .as("La página actual debe ser >= 1")
                .isGreaterThanOrEqualTo(1);
        assertThat(totalUsuarios)
                .as("El total de usuarios debe ser >= 0")
                .isGreaterThanOrEqualTo(0);
        assertThat(totalPaginas)
                .as("El total de páginas debe ser >= 0")
                .isGreaterThanOrEqualTo(0);
    }

    // ===== VALIDACIONES de DTO =====

    @And("la respuesta debe contener errores de validación para {string}")
    public void laRespuestaDebeContenerErroresDeValidacionParaCampo(String campo) {
        // Extraemos la lista bajo la clave "errores"
        Object raw = response.jsonPath().get("errores");
        assertThat(raw)
                .as("La respuesta no contiene 'errores'. Body: " + response.getBody().asString())
                .isNotNull();

        List<Map<String, Object>> errores = response.jsonPath().getList("errores");
        assertThat(errores)
                .as("Debe haber al menos una violación. Body: " + response.getBody().asString())
                .isNotEmpty();

        // Buscamos un error cuya propiedad 'campo' coincida
        boolean encontrado = errores.stream()
                .anyMatch(e -> campo.equalsIgnoreCase(String.valueOf(e.get("campo"))));

        assertThat(encontrado)
                .as("Debe contener una violación para el campo '%s'. Body: %s",
                        campo, response.getBody().asString())
                .isTrue();
    }
    // ===== PUT GENÉRICO =====

    @When("realizo una petición PUT a {string} con body:")
    public void realizoUnaPeticionPUTConBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }

    // ===== ASERCIÓN ESPECÍFICA DE ACTUALIZACIÓN =====

    @And("la respuesta debe contener el usuario actualizado con nombre {string} y email {string}")
    public void laRespuestaDebeContenerUsuarioActualizado(String nombreEsperado, String emailEsperado) {
        response.then()
                .body("id", notNullValue())
                .body("nombre", equalTo(nombreEsperado))
                .body("email", equalTo(emailEsperado))
                .body("fechaActualizacion", notNullValue());
    }

    // ===== PATCH GENÉRICO =====

    @When("realizo una petición PATCH a {string} con body:")
    public void realizoUnaPeticionPATCHConBody(String path, String body) {
        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch(path)
                .then()
                .extract()
                .response();
    }

    // ===== ASERCIÓN ESPECÍFICA DE PATCH =====

    @And("la respuesta debe contener el usuario parcialmente actualizado con campo {string} valor {string}")
    public void laRespuestaDebeContenerUsuarioParcialmenteActualizado(String campo, String valor) {
        // Comprueba que el campo dado tenga el nuevo valor
        response.then()
                .body(campo, equalTo(valor))
                .body("id", notNullValue());
    }
    // ===== DELETE GENÉRICO =====

    @When("realizo una petición DELETE a {string}")
    public void realizoUnaPeticionDELETEA(String path) {
        response = given()
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
    }

    // ===== ASERCIÓN DE NO CONTENT =====

    @And("la respuesta no debe contener contenido")
    public void laRespuestaNoDebeContenerContenido() {
        String body = response.getBody().asString();
        assertThat(body)
                .as("El cuerpo de la respuesta debe estar vacío cuando no hay contenido")
                .isEmpty();
    }


}







