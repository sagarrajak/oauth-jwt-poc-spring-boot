package com.debaterr.app.authresouce.config.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${app.auth.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.resourceserver.issuers.google.issuer-uri}")
    private String googleJwtDecoderUri;

    @Value("${app.auth.debater.secret-key}")
    private String ourSecretKey;

    private final MultiIssueJwtConverter jwtConverter;

    public SecurityConfig(MultiIssueJwtConverter jwtConverter) {
        this.jwtConverter = jwtConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz ->
                authz.requestMatchers("/auth/*")
                        .permitAll().anyRequest().authenticated()
        )
        .oauth2ResourceServer(ouath2 -> ouath2.jwt(jwt -> {
            jwt.jwtAuthenticationConverter(jwtConverter);
            jwt.decoder(getJwtDecoder());
        }))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtDecoder googleJwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }

    @Bean
    public JwtDecoder ourSecretKeyJwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                ourSecretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .build();
    }

    @Bean JwtDecoder getJwtDecoder() {
        return new MultiIssueJwtDecoder(googleJwtDecoder(), ourSecretKeyJwtDecoder());
    }
}
