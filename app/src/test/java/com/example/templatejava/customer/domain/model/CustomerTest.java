package com.example.templatejava.customer.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.domain.exception.InvalidCustomerException;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerTest {

    @Test
    @DisplayName("should create customer successfully with valid attributes")
    void shouldCreateCustomerSuccessfully() {
        Instant now = Instant.now();
        Customer customer = Customer.create("cust-123", "John Doe", "john.doe@example.com", now);

        assertThat(customer.getId()).isEqualTo("cust-123");
        assertThat(customer.getName()).isEqualTo("John Doe");
        assertThat(customer.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.PENDING_BUREAU_ENRICHMENT);
        assertThat(customer.getBureauScore()).isNull();
        assertThat(customer.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should enrich customer with valid bureau score and status")
    void shouldEnrichCustomerWithValidScore() {
        Customer customer =
                Customer.create("cust-123", "John Doe", "john.doe@example.com", Instant.now());

        customer.enrichWithBureauData(750, CustomerStatus.ACTIVE);

        assertThat(customer.getBureauScore()).isEqualTo(750);
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("should throw InvalidCustomerException when bureau score is invalid")
    void shouldThrowWhenScoreIsInvalid() {
        Customer customer =
                Customer.create("cust-123", "John Doe", "john.doe@example.com", Instant.now());

        assertThatThrownBy(() -> customer.enrichWithBureauData(-1, CustomerStatus.ACTIVE))
                .isInstanceOf(InvalidCustomerException.class)
                .hasMessage("Bureau score must be between 0 and 1000");

        assertThatThrownBy(() -> customer.enrichWithBureauData(1001, CustomerStatus.ACTIVE))
                .isInstanceOf(InvalidCustomerException.class)
                .hasMessage("Bureau score must be between 0 and 1000");
    }

    @Test
    @DisplayName("should throw NullPointerException when id is null")
    void shouldThrowWhenIdIsNull() {
        assertThatThrownBy(() -> Customer.create(null, "John", "john@example.com", Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when name is null")
    void shouldThrowWhenNameIsNull() {
        assertThatThrownBy(() -> Customer.create("id", null, "john@example.com", Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("name must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw InvalidCustomerException when name is blank")
    void shouldThrowWhenNameIsBlank(String blankName) {
        assertThatThrownBy(
                        () -> Customer.create("id", blankName, "john@example.com", Instant.now()))
                .isInstanceOf(InvalidCustomerException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    @DisplayName("should throw NullPointerException when email is null")
    void shouldThrowWhenEmailIsNull() {
        assertThatThrownBy(() -> Customer.create("id", "John", null, Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("email must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "invalid-email", "test-without-at"})
    @DisplayName("should throw InvalidCustomerException when email is invalid")
    void shouldThrowWhenEmailIsInvalid(String invalidEmail) {
        assertThatThrownBy(() -> Customer.create("id", "John", invalidEmail, Instant.now()))
                .isInstanceOf(InvalidCustomerException.class)
                .hasMessage("Invalid email address: " + invalidEmail);
    }

    @Test
    @DisplayName("should throw NullPointerException when createdAt is null")
    void shouldThrowWhenCreatedAtIsNull() {
        assertThatThrownBy(() -> Customer.create("id", "John", "john@example.com", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("createdAt must not be null");
    }
}
