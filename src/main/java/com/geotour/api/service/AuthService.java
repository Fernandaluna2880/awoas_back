package com.geotour.api.service;

import com.geotour.api.dto.AuthResponseDTO;
import com.geotour.api.dto.LoginDTO;
import com.geotour.api.dto.UsuarioDTO;
import com.geotour.api.entity.Rol;
import com.geotour.api.entity.Usuario;
import com.geotour.api.entity.enums.ERol;
import com.geotour.api.exception.ConflictException;
import com.geotour.api.exception.ResourceNotFoundException;
import com.geotour.api.mapper.DtoMapper;
import com.geotour.api.repository.RolRepository;
import com.geotour.api.repository.UsuarioRepository;
import com.geotour.api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final DtoMapper dtoMapper;

    @Transactional
    public AuthResponseDTO register(UsuarioDTO dto) {
        String correo = normalizarCorreo(dto.correo());

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("El correo ya se encuentra registrado");
        }

        Rol rolUsuario = rolRepository.findByNombre(ERol.ROLE_USER)
                .orElseGet(() -> rolRepository.save(
                        Rol.builder().nombre(ERol.ROLE_USER).build()
                ));

        Usuario usuario = Usuario.builder()
                .nombreCompleto(dto.nombreCompleto().trim())
                .correo(correo)
                .password(passwordEncoder.encode(dto.password()))
                .rol(rolUsuario)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return buildAuthResponse(usuarioGuardado, jwtUtils.generateToken(usuarioGuardado));
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginDTO dto) {
        String correo = normalizarCorreo(dto.correo());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, dto.password())
        );

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildAuthResponse(usuario, jwtUtils.generateToken(usuario));
    }

    private AuthResponseDTO buildAuthResponse(Usuario usuario, String token) {
        return new AuthResponseDTO(
                token,
                "Bearer",
                jwtUtils.getExpirationSeconds(),
                dtoMapper.toUsuarioResponse(usuario)
        );
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
