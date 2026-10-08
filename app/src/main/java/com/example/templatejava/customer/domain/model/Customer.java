package com.example.templatejava.customer.domain.model;

import com.example.templatejava.customer.domain.exception.InvalidCustomerException;
import java.time.Instant;
import java.util.Objects;

public class Customer {

    public enum CustomerStatus {
        PENDING_BUREAU_ENRICHMENT,
        ACTIVE,
        SUSPENDED
    }

    private final String id;
    private final String name;
    private final String email;
    private final Instant createdAt;
    private CustomerStatus status;
    private Integer bureauScore;

    public Customer(
            String id,
            String name,
            String email,
            CustomerStatus status,
            Integer bureauScore,
            Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.bureauScore = bureauScore;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Customer create(String id, String name, String email, Instant createdAt) {
        return new Customer(
                id, name, email, CustomerStatus.PENDING_BUREAU_ENRICHMENT, null, createdAt);
    }

    public void enrichWithBureauData(int score, CustomerStatus newStatus) {
        if (score < 0 || score > 1000) {
            throw new InvalidCustomerException("Bureau score must be between 0 and 1000");
        }
        this.bureauScore = score;
        this.status = Objects.requireNonNull(newStatus, "newStatus must not be null");
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new InvalidCustomerException("name must not be blank");
        }
        return name;
    }

    private static String validateEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        if (email.isBlank() || !email.contains("@")) {
            throw new InvalidCustomerException("Invalid email address: " + email);
        }
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Integer getBureauScore() {
        return bureauScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
