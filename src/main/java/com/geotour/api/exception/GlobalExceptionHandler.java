package com.geotour.api.exception;

import com.geotour.api.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ApiResponse(
            responseCode = "400",
            description = "Errores de validacion en la peticion",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Errores de validacion en la peticion",
                errors,
                request
        );
    }

    @ApiResponse(
            responseCode = "400",
            description = "Errores de validacion en la peticion",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .distinct()
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Errores de validacion en la peticion",
                errors,
                request
        );
    }

    @ApiResponse(
            responseCode = "400",
            description = "El cuerpo de la peticion no es valido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la peticion no es valido",
                List.of("Verifique el formato y los tipos de datos enviados"),
                request
        );
    }

    @ApiResponse(
            responseCode = "400",
            description = "Parametro de peticion invalido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Parametro de peticion invalido",
                List.of("El parametro '" + exception.getName() + "' tiene un formato invalido"),
                request
        );
    }

    @ApiResponse(
            responseCode = "404",
            description = "Recurso no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), List.of(), request);
    }

    @ApiResponse(
            responseCode = "409",
            description = "Conflicto con el estado actual del recurso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), List.of(), request);
    }

    @ApiResponse(
            responseCode = "400",
            description = "Solicitud invalida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), request);
    }

    @ApiResponse(
            responseCode = "401",
            description = "Credenciales invalidas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "Credenciales invalidas",
                List.of(),
                request
        );
    }

    @ApiResponse(
            responseCode = "401",
            description = "No autenticado o token invalido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "No autenticado o token invalido",
                List.of(),
                request
        );
    }

    @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para realizar esta accion",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta accion",
                List.of(),
                request
        );
    }

    @ApiResponse(
            responseCode = "409",
            description = "La operacion viola una restriccion de integridad de datos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn("Violacion de integridad de datos: {}", exception.getMostSpecificCause().getMessage());
        return build(
                HttpStatus.CONFLICT,
                "La operacion viola una restriccion de integridad de datos",
                List.of(),
                request
        );
    }

    @ApiResponse(
            responseCode = "502",
            description = "Error de integracion con servicio externo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponseDTO> handleIntegration(
            IntegrationException exception,
            HttpServletRequest request
    ) {
        log.warn("Error de integracion con servicio externo: {}", exception.getMessage());
        return build(HttpStatus.BAD_GATEWAY, exception.getMessage(), List.of(), request);
    }

    @ApiResponse(
            responseCode = "405",
            description = "Metodo HTTP no soportado para esta ruta",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Metodo HTTP no soportado para esta ruta",
                List.of(),
                request
        );
    }

    @ApiResponse(
            responseCode = "400",
            description = "Parametro obligatorio ausente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Parametro obligatorio ausente",
                List.of("El parametro '" + exception.getParameterName() + "' es obligatorio"),
                request
        );
    }

    @ApiResponse(
            responseCode = "404",
            description = "Recurso no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResource(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", List.of(), request);
    }

    @ApiResponse(
            responseCode = "415",
            description = "Tipo de contenido no soportado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMediaType(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de contenido no soportado",
                List.of("Use application/json como tipo de contenido"),
                request
        );
    }

    @ApiResponse(
            responseCode = "500",
            description = "Ocurrio un error inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Error no controlado en la peticion {}", request.getRequestURI(), exception);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error inesperado",
                List.of(),
                request
        );
    }

    private ResponseEntity<ErrorResponseDTO> build(
            HttpStatus status,
            String message,
            List<String> errors,
            HttpServletRequest request
    ) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status).body(body);
    }
}
