package com.geotour.api.controller;

import com.geotour.api.dto.DestinoDTO;
import com.geotour.api.dto.DestinoResponseDTO;
import com.geotour.api.dto.ErrorResponseDTO;
import com.geotour.api.service.DestinoService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/destinos")
@RequiredArgsConstructor
@Tag(name = "Destinos", description = "Catalogo y administracion de destinos turisticos")
public class DestinoController {

    private final DestinoService destinoService;

    @Operation(
            summary = "Listar todos los destinos",
            description = "Devuelve el catalogo completo de destinos turisticos. Acceso publico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de destinos",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DestinoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirements(value = {})
    @GetMapping
    public List<DestinoResponseDTO> listar() {
        return destinoService.listar();
    }

    @Operation(
            summary = "Obtener un destino por su identificador",
            description = "Recupera un destino concreto del catalogo. Acceso publico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Destino encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestinoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador invalido",
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
    @GetMapping("/{id}")
    public DestinoResponseDTO obtener(
            @Parameter(description = "Identificador unico del destino", example = "1", required = true)
            @PathVariable Long id
    ) {
        return destinoService.obtener(id);
    }

    @Operation(
            summary = "Crear un destino (ROLE_ADMIN)",
            description = "Crea un nuevo destino en el catalogo. Requiere token JWT con rol ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Destino creado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestinoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del destino invalidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no tiene rol ROLE_ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un destino con ese nombre",
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
    public DestinoResponseDTO crear(@Valid @RequestBody DestinoDTO dto) {
        return destinoService.crear(dto);
    }

    @Operation(
            summary = "Actualizar un destino (ROLE_ADMIN)",
            description = "Actualiza los datos de un destino existente. Requiere token JWT con rol ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Destino actualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestinoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del destino o identificador invalidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no tiene rol ROLE_ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Destino no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un destino con ese nombre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public DestinoResponseDTO actualizar(
            @Parameter(description = "Identificador unico del destino a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody DestinoDTO dto
    ) {
        return destinoService.actualizar(id, dto);
    }

    @Operation(
            summary = "Eliminar un destino (ROLE_ADMIN)",
            description = "Elimina un destino del catalogo. Requiere token JWT con rol ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Destino eliminado correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no tiene rol ROLE_ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Destino no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El destino tiene resenas o reservas asociadas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "Identificador unico del destino a eliminar", example = "1", required = true)
            @PathVariable Long id
    ) {
        destinoService.eliminar(id);
    }
}
