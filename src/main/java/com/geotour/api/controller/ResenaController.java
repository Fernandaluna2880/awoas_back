package com.geotour.api.controller;

import com.geotour.api.dto.ErrorResponseDTO;
import com.geotour.api.dto.ResenaDTO;
import com.geotour.api.dto.ResenaResponseDTO;
import com.geotour.api.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/destinos/{destinoId}/resenas", "/api/v1/destinos/{destinoId}/reseñas"})
@RequiredArgsConstructor
@Tag(name = "Resenas", description = "Opiniones de los usuarios sobre los destinos")
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(
            summary = "Listar las resenas de un destino (publico)",
            description = "Devuelve todas las resenas publicadas para un destino. No requiere autenticacion."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de resenas del destino",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResenaResponseDTO.class)))
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
            )
    })
    @SecurityRequirements(value = {})
    @GetMapping
    public List<ResenaResponseDTO> listar(
            @Parameter(description = "Identificador del destino cuyas resenas se listaran", example = "1", required = true)
            @PathVariable Long destinoId
    ) {
        return resenaService.listarPorDestino(destinoId);
    }

    @Operation(
            summary = "Crear una resena para un destino (ROLE_USER)",
            description = "Publica una resena para el destino indicado usando el usuario autenticado del token JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Resena creada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la resena o identificador invalidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no tiene rol ROLE_USER",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Destino no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El usuario ya publico una resena para este destino",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponseDTO crear(
            @Parameter(description = "Identificador del destino que se resenara", example = "1", required = true)
            @PathVariable Long destinoId,
            @Valid @RequestBody ResenaDTO dto,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return resenaService.crear(destinoId, dto, authentication.getName());
    }
}
