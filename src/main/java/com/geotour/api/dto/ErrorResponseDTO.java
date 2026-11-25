package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Respuesta estandar de error devuelta por el controlador global de excepciones")
public record ErrorResponseDTO(
        @Schema(description = "Instancia UTC en que ocurrio el error", example = "2026-11-11T15:30:00Z")
        Instant timestamp,

        @Schema(description = "Codigo HTTP del error", example = "404")
        int status,

        @Schema(description = "Razon del codigo HTTP", example = "Not Found")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "Destino no encontrado con id 99")
        String message,

        @Schema(description = "Ruta de la peticion que genero el error", example = "/api/v1/destinos/99")
        String path,

        @Schema(description = "Detalles adicionales de validacion u otros errores")
        List<String> errors
) {
}
