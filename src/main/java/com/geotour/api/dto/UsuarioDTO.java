package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un nuevo usuario con rol ROLE_USER")
public record UsuarioDTO(
        @Schema(
                description = "Nombre completo del usuario",
                example = "Jose Alonso Macias",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 3,
                maxLength = 100
        )
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
        String nombreCompleto,

        @Schema(
                description = "Correo electronico unico del usuario",
                example = "usuario@geotour.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String correo,

        @Schema(
                description = "Contrasena del usuario",
                example = "Admin1234!",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
