package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Datos necesarios para crear una reserva turistica")
public record ReservaDTO(
        @Schema(
                description = "Identificador del destino a reservar",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "El destino es obligatorio")
        Long destinoId,

        @Schema(
                description = "Fecha de la reserva, no puede ser anterior a hoy",
                example = "2026-12-15",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date"
        )
        @NotNull(message = "La fecha de la reserva es obligatoria")
        @FutureOrPresent(message = "La fecha de la reserva no puede ser anterior a hoy")
        LocalDate fechaReserva,

        @Schema(
                description = "Numero de personas incluidas en la reserva",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "1"
        )
        @NotNull(message = "El numero de personas es obligatorio")
        @Min(value = 1, message = "El número de personas debe ser al menos 1")
        Integer numeroAsistentes
) {
}
