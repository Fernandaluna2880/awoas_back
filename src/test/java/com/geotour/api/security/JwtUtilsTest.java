package com.geotour.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private static final String SECRETO_HEX =
            "5f4dcc3b5aa765d61d8327deb882cf995f4dcc3b5aa765d61d8327deb882cf99";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRETO_HEX);
        ReflectionTestUtils.setField(jwtUtils, "expirationMs", 3_600_000L);
        jwtUtils.init();
    }

    @Test
    void tokenGeneradoUsaHmacSha256YEsValido() {
        UserDetails userDetails = User.withUsername("user@geotour.com")
                .password("no-usada")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtils.generateToken(userDetails);
        SecretKey key = Keys.hmacShaKeyFor(HexFormat.of().parseHex(SECRETO_HEX));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("user@geotour.com");
        assertThat(claims.get("roles")).isEqualTo(java.util.List.of("ROLE_USER"));
        assertThat(jwtUtils.extractUsername(token)).isEqualTo("user@geotour.com");
        assertThat(jwtUtils.isTokenValid(token)).isTrue();
        assertThat(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getHeader()
                .getAlgorithm()).isEqualTo("HS256");
    }

    @Test
    void tokenManipuladoNoEsValido() {
        assertThat(jwtUtils.isTokenValid("token.no.valido")).isFalse();
    }
}
