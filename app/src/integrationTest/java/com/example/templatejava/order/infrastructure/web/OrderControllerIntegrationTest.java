package com.example.templatejava.order.infrastructure.web;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.AbstractIntegrationTest;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import com.example.templatejava.order.infrastructure.web.request.CreateOrderRequest;
import com.example.templatejava.order.infrastructure.web.response.OrderResponse;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("should create and retrieve order successfully using RestAssured")
    void shouldCreateAndRetrieveOrderSuccessfully() {
        // 1. Create a customer to ensure customer is eligible
        var customerRequest = new CreateCustomerRequest("Alice Order", "alice.order@example.com");
        CustomerResponse customer =
                given().contentType(ContentType.JSON)
                        .body(customerRequest)
                        .when()
                        .post("/customers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(CustomerResponse.class);

        // 2. Create an order for the newly created customer
        var orderRequest = new CreateOrderRequest(customer.id(), new BigDecimal("199.99"));
        OrderResponse orderResponse =
                given().contentType(ContentType.JSON)
                        .body(orderRequest)
                        .when()
                        .post("/orders")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(OrderResponse.class);

        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.id()).isNotNull().isNotBlank();
        assertThat(orderResponse.customerId()).isEqualTo(customer.id());
        assertThat(orderResponse.amount()).isEqualByComparingTo("199.99");
        assertThat(orderResponse.status()).isEqualTo("CREATED");

        // 3. Retrieve the order by ID
        OrderResponse fetchedOrder =
                given().noContentType()
                        .when()
                        .get("/orders/" + orderResponse.id())
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(OrderResponse.class);

        assertThat(fetchedOrder).isNotNull();
        assertThat(fetchedOrder.id()).isEqualTo(orderResponse.id());
        assertThat(fetchedOrder.customerId()).isEqualTo(customer.id());
        assertThat(fetchedOrder.amount()).isEqualByComparingTo("199.99");
        assertThat(fetchedOrder.status()).isEqualTo("CREATED");
    }

    @Test
    @DisplayName("should return 400 Bad Request when request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() {
        given().contentType(ContentType.JSON)
                .body("{ invalid_json }")
                .when()
                .post("/orders")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("should return 404 Not Found when order does not exist")
    void shouldReturn404WhenOrderDoesNotExist() {
        given().noContentType().when().get("/orders/non-existent-id").then().statusCode(404);
    }

    @Test
    @DisplayName("should return 422 Unprocessable Entity when order data is invalid")
    void shouldReturn422WhenOrderDataIsInvalid() {
        var request = new CreateOrderRequest("valid-customer-id", new BigDecimal("-10.00"));

        given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/orders")
                .then()
                .statusCode(422);
    }
}
