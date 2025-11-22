package com.debaterr.app.authresouce.service;

import com.debaterr.app.authresouce.entity.AuthUser;
import com.debaterr.app.authresouce.pojo.AuthResponse;
import com.debaterr.app.authresouce.pojo.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final AuthenticationManager manager;
    private final JWTService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(AuthenticationManager manager, JWTService jwtService, UserService userService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.manager = manager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void verifyUserLogin(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public void signup(SignupRequest signupRequest) {
        // Encode password
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Create and save user
        AuthUser user = AuthUser.builder()
                .username(signupRequest.getUsername())
                .password(encodedPassword)
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .isEnabled(true)
                .isEmailVerified(true) // TODO:: verify email using mail send
                .build();

        this.sendWelcomeEmail(user);
        userService.saveUser(user);
    }

    private void sendWelcomeEmail(AuthUser user) {
        try {
            String subject = "Welcome to Debaterr!";
            String body = String.format(
                    "Dear %s,\n\nWelcome to Debaterr! Your account has been successfully created.\n\nUsername: %s\nEmail: %s\n\nBest regards,\nDebaterr Team",
                    user.getFirstName(),
                    user.getUsername(),
                    user.getEmail()
            );

            emailService.sendEmail(user.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    public AuthResponse login(String username, String password) {
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        AuthUser user = (AuthUser) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn((long) (24 * 60 * 60)) // 24 hours in seconds
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
