package com.geotour.api.controller;

import com.geotour.api.dto.ErrorResponseDTO;
import com.geotour.api.dto.ReservaDTO;
import com.geotour.api.dto.ReservaResponseDTO;
import com.geotour.api.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestion de reservas turisticas")
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(
            summary = "Crear una reserva para el usuario autenticado (ROLE_USER)",
            description = "Crea una reserva, valida la capacidad disponible del destino y calcula el precio total."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva creada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la reserva invalidos o capacidad insuficiente",
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
                    description = "Conflicto al crear la reserva",
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
    public ReservaResponseDTO crear(
            @Valid @RequestBody ReservaDTO dto,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return reservaService.crear(dto, authentication.getName());
    }

    @Operation(
            summary = "Listar las reservas del usuario autenticado (ROLE_USER)",
            description = "Devuelve las reservas del usuario asociado al token JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de reservas del usuario autenticado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
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
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/mis-reservas")
    public List<ReservaResponseDTO> listarMisReservas(@Parameter(hidden = true) Authentication authentication) {
        return reservaService.listarMisReservas(authentication.getName());
    }

    @Operation(
            summary = "Cancelar una reserva propia (ROLE_USER) o cualquier reserva (ROLE_ADMIN)",
            description = "Cambia el estado de la reserva a CANCELADA. El dueño o un administrador pueden cancelarla."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva cancelada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador invalido o reserva ya cancelada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente o invalido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "La reserva pertenece a otro usuario",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/cancelar")
    public ReservaResponseDTO cancelar(
            @Parameter(description = "Identificador unico de la reserva a cancelar", example = "25", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return reservaService.cancelar(id, authentication.getName(), esAdmin);
    }

    @Operation(
            summary = "Listar todas las reservas (ROLE_ADMIN)",
            description = "Devuelve todas las reservas del sistema. Requiere token JWT con rol ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado completo de reservas",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
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
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<ReservaResponseDTO> listarTodas() {
        return reservaService.listarTodas();
    }

    @Operation(
            summary = "Eliminar una reserva (ROLE_ADMIN)",
            description = "Elimina una reserva del sistema. Requiere token JWT con rol ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada correctamente"),
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
                    description = "Reserva no encontrada",
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
            @Parameter(description = "Identificador unico de la reserva a eliminar", example = "25", required = true)
            @PathVariable Long id
    ) {
        reservaService.eliminar(id);
    }
}
