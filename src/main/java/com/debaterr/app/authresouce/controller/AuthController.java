package com.debaterr.app.authresouce.controller;

import com.debaterr.app.authresouce.pojo.AuthLoginRequest;
import com.debaterr.app.authresouce.pojo.AuthResponse;
import com.debaterr.app.authresouce.pojo.SignupRequest;
import com.debaterr.app.authresouce.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
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
        log.info("user coming here");
        log.info("username {} password {}", authLoginRequest.getUsername(), authLoginRequest.getPassword());
        return authService.login(authLoginRequest.getUsername(), authLoginRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity singUp(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

}
