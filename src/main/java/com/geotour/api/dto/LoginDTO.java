package com.geotour.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credenciales para iniciar sesion")
public record LoginDTO(
        @Schema(
                description = "Correo electronico registrado",
                example = "usuario@geotour.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150,
                format = "email"
        )
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String correo,

        @Schema(
                description = "Contrasena del usuario",
                example = "Admin1234!",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 72,
                format = "password"
        )
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(max = 72, message = "La contrasena no puede superar los 72 caracteres")
        String password
) {
}
