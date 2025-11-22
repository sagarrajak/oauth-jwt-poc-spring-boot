package com.debaterr.app.authresouce.config.auth;


import com.debaterr.app.authresouce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class MultiIssueJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${app.auth.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.resourceserver.issuers.google.issuer-uri}")
    private String googleJwtDecoderUri;

    @Value("${spring.security.oauth2.resourceserver.issuers.debaterr.issuer-uri}")
    private String debaterrDecoderUri;

    private final UserService userService;

    public MultiIssueJwtConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        log.info("passed jwt is {}", jwt);
        String issuer = jwt.getClaim("iss").toString();
        Collection<GrantedAuthority> authority = getAuthority(jwt, issuer);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, authority);
        jwtAuthenticationToken.setDetails(buildAuthenticationDetails(jwt, issuer));
        // TODO: set user details from db
        return jwtAuthenticationToken;
    }

    private Collection<GrantedAuthority> getAuthority(Jwt jwt, String issuer) {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        if (googleJwtDecoderUri.equals(issuer)) {
            // Extract Google-specific authorities
            String email = jwt.getClaimAsString("email");
            if (email != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GOOGLE_USER"));
            }
            // Check audience
            if (jwt.getAudience() != null && jwt.getAudience().contains(googleClientId)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_VALID_GOOGLE_CLIENT"));
            }
        }
        else if (debaterrDecoderUri.equals(issuer)) {
            String email = jwt.getClaimAsString("email");
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }


    private Map<String, Object> buildAuthenticationDetails(Jwt jwt, String issuer) {
        Map<String, Object> details = new HashMap<>();
        details.put("issuer", issuer);
        details.put("tokenType", "JWT");
        details.put("issuedAt", jwt.getIssuedAt());
        details.put("expiresAt", jwt.getExpiresAt());
        var foundUser = userService.findUserByEmail(jwt.getClaimAsString("email"));
        details.put("user", foundUser);
        if ("https://accounts.google.com".equals(issuer)) {
            details.put("provider", "GOOGLE");
            details.put("email", jwt.getClaimAsString("email"));
            details.put("name", jwt.getClaimAsString("name"));
        } else if ("https://www.facebook.com".equals(issuer)) {
            details.put("provider", "META");
            details.put("user_id", jwt.getClaimAsString("user_id"));
        }
        else if ("https://www.debaterr.com".equals(issuer)) {
            details.put("provider", "DEBATER");
            details.put("user_id", jwt.getClaimAsString("user_id"));
        }
        return details;
    }
}
