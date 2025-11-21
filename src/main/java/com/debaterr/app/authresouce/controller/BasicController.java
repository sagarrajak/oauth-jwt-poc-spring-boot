package com.debaterr.app.authresouce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class BasicController {

    @GetMapping("profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> profile = new HashMap<>();

        String issuer = jwt.getClaim("iss");
        profile.put("issuer", issuer);
        profile.put("subject", jwt.getSubject());
        profile.put("email", jwt.getClaimAsString("email"));
        profile.put("name", jwt.getClaimAsString("name"));
        profile.put("provider", getProviderFromIssuer(issuer));
        return ResponseEntity.ok(profile);
    }

    private String getProviderFromIssuer(String issuer) {
        if ("https://accounts.google.com".equals(issuer)) {
            return "GOOGLE";
        } else if ("https://www.facebook.com".equals(issuer)) {
            return "META";
        } else if ("https://debaterr.com".equals(issuer)) {
            return "INTERNAL";
        }
        return "UNKNOWN";
    }
}
