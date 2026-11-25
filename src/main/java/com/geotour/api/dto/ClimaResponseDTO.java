package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Informacion climatologica actual de un destino obtenida de OpenWeather")
public record ClimaResponseDTO(
        @Schema(description = "Identificador del destino consultado", example = "1")
        Long destinoId,

        @Schema(description = "Nombre del destino consultado", example = "Machu Picchu")
        String destinoNombre,

        @Schema(description = "Temperatura actual en grados Celsius", example = "18.5")
        Double temperatura,

        @Schema(description = "Sensacion termica en grados Celsius", example = "17.2")
        Double sensacionTermica,

        @Schema(description = "Humedad relativa en porcentaje", example = "72")
        Integer humedad,

        @Schema(description = "Velocidad del viento en metros por segundo", example = "3.1")
        Double velocidadViento,

        @Schema(description = "Descripcion del estado del tiempo", example = "nubes dispersas")
        String descripcion,

        @Schema(description = "Codigo de icono devuelto por OpenWeather", example = "02d")
        String icono,

        @Schema(description = "Instante UTC en que se realizo la consulta", example = "2026-11-11T15:30:00Z")
        Instant consultadoEn
) {
}
