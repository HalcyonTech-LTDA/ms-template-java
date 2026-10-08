package com.example.templatejava.order.infrastructure.web.exception;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import com.example.templatejava.order.domain.exception.CustomerNotEligibleException;
import com.example.templatejava.order.domain.exception.InvalidOrderException;
import com.example.templatejava.order.domain.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.templatejava.order.infrastructure.web")
@Order(1)
public class OrderWebExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            OrderNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "ORDER_NOT_FOUND",
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CustomerNotEligibleException.class)
    public ResponseEntity<ErrorResponse> handleNotEligible(
            CustomerNotEligibleException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "CUSTOMER_NOT_ELIGIBLE",
                        ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrder(
            InvalidOrderException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "INVALID_ORDER",
                        ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }
}
