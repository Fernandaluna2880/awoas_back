package com.geotour.api.controller;

import com.geotour.api.dto.ErrorResponseDTO;
import com.geotour.api.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints administrativos para la moderacion de resenas.
 * Se expone tanto la ruta ASCII como la ruta con ene para mantener
 * compatibilidad con el contrato del enunciado.
 */
@RestController
@RequestMapping({"/api/v1/resenas", "/api/v1/reseñas"})
@RequiredArgsConstructor
@Tag(name = "Resenas (Administracion)", description = "Moderacion de resenas por parte del administrador")
public class ResenaAdminController {

    private final ResenaService resenaService;

    @Operation(
            summary = "Eliminar una reseña propia (ROLE_USER) o cualquier resena (ROLE_ADMIN)",
            description = "Elimina una resena por su identificador. Solo el autor o un administrador pueden eliminarla."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resena eliminada correctamente"),
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
                    description = "Resena no encontrada",
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
            @Parameter(description = "Identificador unico de la resena a eliminar", example = "10", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        resenaService.eliminar(id, authentication.getName(), esAdmin);
    }
}
