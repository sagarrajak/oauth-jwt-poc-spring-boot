package com.debaterr.app.authresouce.config.auth;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
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


    public JwtDecoder googleJwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }


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

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        SecretKey secretKey = new SecretKeySpec(
                ourSecretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        // wrap the SecretKey in a Nimbus JWK
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(new Algorithm("HS256"))
                .build();

        return new ImmutableJWKSet<>(new JWKSet(jwk));
    }

    /* 2. Make NimbusJwtEncoder injectable */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    PasswordEncoder getPasswordEncode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
