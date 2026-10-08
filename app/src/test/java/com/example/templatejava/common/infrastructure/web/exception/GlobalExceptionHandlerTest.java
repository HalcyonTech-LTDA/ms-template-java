package com.example.templatejava.common.infrastructure.web.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    @DisplayName("should handle IllegalArgumentException with 400")
    void shouldHandleIllegalArgument() {
        var ex = new IllegalArgumentException("Bad argument");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("INVALID_ARGUMENT");
        assertThat(response.getBody().message()).isEqualTo("Bad argument");
    }

    @Test
    @DisplayName("should handle generic Exception with 500")
    void shouldHandleGeneralException() {
        var ex = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Internal Server Error");
    }
}
