package com.example.templatejava.customer.infrastructure.web;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.AbstractIntegrationTest;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("should create customer successfully and return 201 Created using RestAssured")
    void shouldCreateCustomerSuccessfully() {
        var request = new CreateCustomerRequest("John Doe", "john.doe@example.com");

        CustomerResponse response =
                given().contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/customers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(CustomerResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull().isNotBlank();
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("should return 400 Bad Request when request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() {
        given().contentType(ContentType.JSON)
                .body("{ invalid_json }")
                .when()
                .post("/customers")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("should return 422 Unprocessable Entity when customer data is invalid")
    void shouldReturn422WhenCustomerDataIsInvalid() {
        var request = new CreateCustomerRequest("", "invalid-email");

        given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/customers")
                .then()
                .statusCode(422);
    }

    @Test
    @DisplayName("should return 409 Conflict when customer email already exists")
    void shouldReturn409WhenCustomerEmailAlreadyExists() {
        var request = new CreateCustomerRequest("Bob Builder", "bob@example.com");

        // Create first time
        given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/customers")
                .then()
                .statusCode(201);

        // Create second time to cause conflict
        given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/customers")
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("should return 404 Not Found when customer does not exist")
    void shouldReturn404WhenCustomerDoesNotExist() {
        given().noContentType().when().get("/customers/non-existent-id").then().statusCode(404);
    }
}
