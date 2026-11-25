package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos publicos de un usuario de la plataforma")
public record UsuarioResponseDTO(
        @Schema(description = "Identificador unico del usuario", example = "2")
        Long id,

        @Schema(description = "Nombre completo del usuario", example = "Jose Alonso Macias")
        String nombreCompleto,

        @Schema(description = "Correo electronico del usuario", example = "usuario@geotour.com", format = "email")
        String correo,

        @Schema(description = "Rol asignado al usuario", example = "ROLE_USER", allowableValues = {"ROLE_USER", "ROLE_ADMIN"})
        String rol
) {
}
