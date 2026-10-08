package com.example.templatejava.customer.infrastructure.web.exception;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import com.example.templatejava.customer.domain.exception.CustomerAlreadyExistsException;
import com.example.templatejava.customer.domain.exception.CustomerNotFoundException;
import com.example.templatejava.customer.domain.exception.InvalidCustomerException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.templatejava.customer.infrastructure.web")
@Order(1)
public class CustomerWebExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            CustomerNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "CUSTOMER_NOT_FOUND",
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(
            CustomerAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "CUSTOMER_ALREADY_EXISTS",
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidCustomerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCustomer(
            InvalidCustomerException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "INVALID_CUSTOMER",
                        ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }
}
