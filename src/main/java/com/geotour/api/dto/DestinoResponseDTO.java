package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Destino turistico devuelto por el catalogo")
public record DestinoResponseDTO(
        @Schema(description = "Identificador unico del destino", example = "1")
        Long id,

        @Schema(description = "Nombre del destino", example = "Machu Picchu")
        String nombre,

        @Schema(description = "Descripcion del destino", example = "Ciudadela inca ubicada en la region de Cusco")
        String descripcion,

        @Schema(description = "Latitud del destino", example = "-13.1631")
        Double latitud,

        @Schema(description = "Longitud del destino", example = "-72.5450")
        Double longitud,

        @Schema(description = "Precio base por persona", example = "250.00")
        BigDecimal precioBasePorPersona,

        @Schema(description = "Capacidad maxima de visitantes", example = "120")
        Integer capacidad
) {
}
