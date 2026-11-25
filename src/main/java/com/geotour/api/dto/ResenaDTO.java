package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para publicar una resena")
public record ResenaDTO(
        @Schema(
                description = "Calificacion del destino de 1 a 5 estrellas",
                example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "1",
                maximum = "5"
        )
        @NotNull(message = "La calificacion es obligatoria")
        @Min(value = 1, message = "La calificacion minima es 1")
        @Max(value = 5, message = "La calificacion maxima es 5")
        Integer calificacion,

        @Schema(
                description = "Comentario de la resena",
                example = "Una experiencia increible, muy recomendable",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 1000
        )
        @NotBlank(message = "El comentario es obligatorio")
        @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
        String comentario
) {
}
