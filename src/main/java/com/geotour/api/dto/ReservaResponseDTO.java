package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Reserva turistica devuelta por la API")
public record ReservaResponseDTO(
        @Schema(description = "Identificador unico de la reserva", example = "25")
        Long id,

        @Schema(description = "Identificador del usuario titular de la reserva", example = "2")
        Long usuarioId,

        @Schema(description = "Nombre del usuario titular de la reserva", example = "Jose Alonso Macias")
        String usuarioNombre,

        @Schema(description = "Identificador del destino reservado", example = "1")
        Long destinoId,

        @Schema(description = "Nombre del destino reservado", example = "Machu Picchu")
        String destinoNombre,

        @Schema(description = "Fecha de la reserva", example = "2026-12-15", format = "date")
        LocalDate fechaReserva,

        @Schema(description = "Numero de personas incluidas en la reserva", example = "2")
        Integer numeroAsistentes,

        @Schema(description = "Precio total calculado de la reserva", example = "500.00")
        BigDecimal precioTotal,

        @Schema(
                description = "Estado actual de la reserva",
                example = "PENDIENTE",
                allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA"}
        )
        String estado
) {
}
