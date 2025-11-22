package com.debaterr.app.authresouce.service;

import com.debaterr.app.authresouce.entity.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWTService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JWTService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateAccessToken(AuthUser authUser) {
        Instant instant = Instant.now();

        JwtClaimsSet.Builder claimBuilder = JwtClaimsSet.builder()
                .issuer("https://www.debaterr.com")
                .issuedAt(Instant.now())
                .expiresAt(instant.plusSeconds(24 * 60 * 60)) // 24 hrs
                .subject(authUser.getUsername())
                .claim("scope", authUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" ")))
                .claim("first_name", authUser.getFirstName())
                .claim("last_name", authUser.getLastName())
                .claim("token_type", "access")
                .claim("username", authUser.getUsername());

        if (authUser.getEmail() != null) {
            claimBuilder.claim("email", authUser.getEmail());
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claimBuilder.build())).getTokenValue();
    }

    public String generateRefreshToken(AuthUser authUser) {
        Instant instant = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("https://www.debaterr.com")
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(20 * 24 * 60 * 60)) // 20 days
                .subject(authUser.getUsername())
                .claim("token_type", "refresh")
                .claim("username", authUser.getUsername())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

}
