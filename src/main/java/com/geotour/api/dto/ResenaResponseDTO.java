package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resena publicada por un usuario sobre un destino")
public record ResenaResponseDTO(
        @Schema(description = "Identificador unico de la resena", example = "10")
        Long id,

        @Schema(description = "Identificador del destino resenado", example = "1")
        Long destinoId,

        @Schema(description = "Nombre del destino resenado", example = "Machu Picchu")
        String destinoNombre,

        @Schema(description = "Identificador del usuario que publico la resena", example = "2")
        Long usuarioId,

        @Schema(description = "Nombre del usuario que publico la resena", example = "Jose Alonso Macias")
        String usuarioNombre,

        @Schema(description = "Calificacion otorgada al destino", example = "5", minimum = "1", maximum = "5")
        Integer calificacion,

        @Schema(description = "Comentario de la resena", example = "Una experiencia increible, muy recomendable")
        String comentario,

        @Schema(description = "Fecha y hora de creacion de la resena")
        LocalDateTime fechaCreacion
) {
}
