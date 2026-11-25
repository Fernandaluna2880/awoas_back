package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para crear o actualizar un destino turistico")
public record DestinoDTO(
        @Schema(
                description = "Nombre del destino",
                example = "Machu Picchu",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150
        )
        @NotBlank(message = "El nombre del destino es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Schema(
                description = "Descripcion del destino",
                example = "Ciudadela inca ubicada en la region de Cusco",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 2000
        )
        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 2000, message = "La descripcion no puede superar los 2000 caracteres")
        String descripcion,

        @Schema(
                description = "Latitud del destino entre -90 y 90",
                example = "-13.1631",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "-90.0",
                maximum = "90.0"
        )
        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
        @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
        Double latitud,

        @Schema(
                description = "Longitud del destino entre -180 y 180",
                example = "-72.5450",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "-180.0",
                maximum = "180.0"
        )
        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
        @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
        Double longitud,

        @Schema(
                description = "Precio base por persona en la moneda local",
                example = "250.00",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0.01"
        )
        @NotNull(message = "El precio base es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a cero")
        BigDecimal precioBasePorPersona,

        @Schema(
                description = "Capacidad maxima de visitantes del destino",
                example = "120",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "1"
        )
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser mayor a cero")
        Integer capacidad
) {
}
