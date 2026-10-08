package com.example.templatejava.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.domain.exception.InvalidOrderException;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTest {

    @Test
    @DisplayName("should create order successfully with valid attributes")
    void shouldCreateOrderSuccessfully() {
        Instant now = Instant.now();
        Order order = Order.create("ord-123", "cust-456", new BigDecimal("99.90"), now);

        assertThat(order.getId()).isEqualTo("ord-123");
        assertThat(order.getCustomerId()).isEqualTo("cust-456");
        assertThat(order.getAmount()).isEqualByComparingTo("99.90");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should confirm order when status is CREATED")
    void shouldConfirmOrder() {
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("50.00"), Instant.now());
        order.confirm();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("should expire order when status is CREATED")
    void shouldExpireOrder() {
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("50.00"), Instant.now());
        order.expire();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EXPIRED);
    }

    @Test
    @DisplayName("should throw IllegalStateException when confirming an expired order")
    void shouldThrowWhenConfirmingExpired() {
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("50.00"), Instant.now());
        order.expire();

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot confirm order in status: EXPIRED");
    }

    @Test
    @DisplayName("should throw IllegalStateException when expiring an already confirmed order")
    void shouldThrowWhenExpiringConfirmed() {
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("50.00"), Instant.now());
        order.confirm();

        assertThatThrownBy(order::expire)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot expire order in status: CONFIRMED");
    }

    @Test
    @DisplayName("should throw NullPointerException when id is null")
    void shouldThrowWhenIdIsNull() {
        assertThatThrownBy(
                        () -> Order.create(null, "cust-1", new BigDecimal("50.00"), Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when customerId is null")
    void shouldThrowWhenCustomerIdIsNull() {
        assertThatThrownBy(
                        () -> Order.create("ord-1", null, new BigDecimal("50.00"), Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerId must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    @DisplayName("should throw InvalidOrderException when customerId is blank")
    void shouldThrowWhenCustomerIdIsBlank(String blankCustomerId) {
        assertThatThrownBy(
                        () ->
                                Order.create(
                                        "ord-1",
                                        blankCustomerId,
                                        new BigDecimal("50.00"),
                                        Instant.now()))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("customerId must not be blank");
    }

    @Test
    @DisplayName("should throw NullPointerException when amount is null")
    void shouldThrowWhenAmountIsNull() {
        assertThatThrownBy(() -> Order.create("ord-1", "cust-1", null, Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("amount must not be null");
    }

    @Test
    @DisplayName("should throw InvalidOrderException when amount is zero or negative")
    void shouldThrowWhenAmountIsZeroOrNegative() {
        assertThatThrownBy(() -> Order.create("ord-1", "cust-1", BigDecimal.ZERO, Instant.now()))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("amount must be greater than zero");

        assertThatThrownBy(
                        () ->
                                Order.create(
                                        "ord-1", "cust-1", new BigDecimal("-10.00"), Instant.now()))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("amount must be greater than zero");
    }
}
