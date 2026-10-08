package com.example.templatejava.order.domain.model;

import com.example.templatejava.order.domain.exception.InvalidOrderException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Order {

    public enum OrderStatus {
        CREATED,
        CONFIRMED,
        EXPIRED
    }

    private final String id;
    private final String customerId;
    private final BigDecimal amount;
    private final Instant createdAt;
    private OrderStatus status;

    public Order(
            String id,
            String customerId,
            BigDecimal amount,
            OrderStatus status,
            Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = validateCustomerId(customerId);
        this.amount = validateAmount(amount);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Order create(String id, String customerId, BigDecimal amount, Instant createdAt) {
        return new Order(id, customerId, amount, OrderStatus.CREATED, createdAt);
    }

    public void confirm() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot confirm order in status: " + status);
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void expire() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot expire order in status: " + status);
        }
        this.status = OrderStatus.EXPIRED;
    }

    private static String validateCustomerId(String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        if (customerId.isBlank()) {
            throw new InvalidOrderException("customerId must not be blank");
        }
        return customerId;
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderException("amount must be greater than zero");
        }
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
