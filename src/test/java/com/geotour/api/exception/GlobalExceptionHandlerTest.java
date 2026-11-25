package com.geotour.api.exception;

import com.geotour.api.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void errorSiempreIncluyeTimestampStatusErrorMessagePathYErrors() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/reservas");

        ResponseEntity<ErrorResponseDTO> response = handler.handleBadRequest(
                new BadRequestException("El numero de personas debe ser al menos 1"),
                request
        );
        ErrorResponseDTO body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body).isNotNull();
        assertThat(body.timestamp()).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("El numero de personas debe ser al menos 1");
        assertThat(body.path()).isEqualTo("/api/v1/reservas");
        assertThat(body.errors()).isNotNull();
    }
}
