package com.debaterr.app.authresouce.config.auth;



import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.debaterr.app.authresouce.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import static org.springframework.security.config.Elements.JWT;

@Component
public class MultiIssueJwtDecoder implements JwtDecoder {
    private final JwtDecoder googleDecoder;
    private final JwtDecoder ourCustomDecoder;
    private final FallbackJwtDecoder fallbackJwtDecoder = new FallbackJwtDecoder();

    @Value("${spring.security.oauth2.resourceserver.issuers.google.issuer-uri}")
    private String googleJwtDecoderUri;

    public MultiIssueJwtDecoder(JwtDecoder googleDecoder,  JwtDecoder ourCustomDecoder) {
        this.googleDecoder = googleDecoder;
        this.ourCustomDecoder = ourCustomDecoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        String issuerFromToken = this.getIssuerFromToken(token);
        try {
            if (issuerFromToken.equals(googleJwtDecoderUri)) {
                return googleDecoder.decode(token);
            }
            else if (issuerFromToken.equals(ourCustomDecoder)) {
                return ourCustomDecoder.decode(token);
            }
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token format", e);
        }
        return null;
    }

    private String getIssuerFromToken(String token) {
        try {
            DecodedJWT decode = com.auth0.jwt.JWT.decode(token);
            String issuer = decode.getClaim("iss").asString();
            if (issuer == null) {
                throw new JwtException("Missing issuer claim in token");
            }
            return issuer;
        } catch (JWTDecodeException e) {
            throw new JwtException("Invalid JWT token format", e);
        }
    }

    private static class FallbackJwtDecoder implements JwtDecoder {
        @Override
        public Jwt decode(String token) throws JwtException {
            throw new JwtException("Unsupported token issuer");
        }
    }


}
