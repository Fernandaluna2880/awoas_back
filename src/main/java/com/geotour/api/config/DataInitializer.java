package com.geotour.api.config;

import com.geotour.api.entity.Rol;
import com.geotour.api.entity.Usuario;
import com.geotour.api.entity.enums.ERol;
import com.geotour.api.repository.RolRepository;
import com.geotour.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email}")
    private String adminEmail;

    @Value("${app.seed.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        Arrays.stream(ERol.values()).forEach(rolNombre ->
                rolRepository.findByNombre(rolNombre)
                        .orElseGet(() -> rolRepository.save(Rol.builder().nombre(rolNombre).build()))
        );

        String correoAdmin = adminEmail.trim().toLowerCase(Locale.ROOT);

        if (!usuarioRepository.existsByCorreoIgnoreCase(correoAdmin)) {
            Rol rolAdmin = rolRepository.findByNombre(ERol.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("No se pudo inicializar el rol ROLE_ADMIN"));

            Usuario admin = Usuario.builder()
                    .nombreCompleto("Administrador GeoTour")
                    .correo(correoAdmin)
                    .password(passwordEncoder.encode(adminPassword))
                    .rol(rolAdmin)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador inicial creado con correo {}", correoAdmin);
        }
    }
}
