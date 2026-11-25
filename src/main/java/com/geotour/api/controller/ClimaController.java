package com.geotour.api.controller;

import com.geotour.api.dto.ClimaResponseDTO;
import com.geotour.api.dto.ErrorResponseDTO;
import com.geotour.api.service.ClimaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destinos/{destinoId}/clima")
@RequiredArgsConstructor
@Tag(name = "Clima", description = "Consulta climatologica en tiempo real por destino")
public class ClimaController {

    private final ClimaService climaService;

    @Operation(
            summary = "Obtener el clima actual de un destino (publico)",
            description = "Consulta el clima en tiempo real usando la API externa OpenWeather. No requiere autenticacion."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Clima actual del destino",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClimaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador de destino invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Destino no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Error de integracion con el proveedor del clima",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirements(value = {})
    @GetMapping
    public ClimaResponseDTO obtenerClima(
            @Parameter(description = "Identificador del destino del que se consultara el clima", example = "1", required = true)
            @PathVariable Long destinoId
    ) {
        return climaService.obtenerClima(destinoId);
    }
}
