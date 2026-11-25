package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticacion con el token JWT y los datos del usuario")
public record AuthResponseDTO(
        @Schema(
                description = "Token JWT firmado que debe enviarse como Authorization: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvQGdlb3RvdXIuY29tIn0.example"
        )
        String token,

        @Schema(description = "Tipo de token devuelto", example = "Bearer", allowableValues = "Bearer")
        String tokenType,

        @Schema(description = "Tiempo de validez del token en segundos", example = "86400")
        long expiresIn,

        @Schema(description = "Datos publicos del usuario autenticado")
        UsuarioResponseDTO usuario
) {
}
