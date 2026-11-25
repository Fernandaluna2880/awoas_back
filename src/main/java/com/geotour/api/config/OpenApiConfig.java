package com.geotour.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Value("${app.openapi.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.openapi.server-description:Servidor local}")
    private String serverDescription;

    @Bean
    public OpenAPI geoTourOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GeoTour API")
                        .description("API REST para la gestion de reservas turisticas, destinos, resenas y consulta del clima. "
                                + "Los endpoints protegidos requieren un token JWT obtenido en /api/v1/auth/login.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GeoTour")
                                .email("soporte@geotour.com")
                        )
                        .license(new License()
                                .name("Uso interno")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(serverDescription)
                ))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_SCHEME,
                                new SecurityScheme()
                                        .name(BEARER_SCHEME)
                                        .description("Token JWT con prefijo Bearer, obtenido mediante /api/v1/auth/login")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
