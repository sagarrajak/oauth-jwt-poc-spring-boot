package com.debaterr.app.authresouce.controller;

import com.debaterr.app.authresouce.pojo.AuthLoginRequest;
import com.debaterr.app.authresouce.pojo.AuthResponse;
import com.debaterr.app.authresouce.pojo.SignupRequest;
import com.debaterr.app.authresouce.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getPublicInfo() {
        return ResponseEntity.ok("This is public information");
    }

    @PostMapping("/login")
    public AuthResponse signup(@RequestBody AuthLoginRequest authLoginRequest) {
        return authService.login(authLoginRequest.getUsername(), authLoginRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity singUp(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

}
