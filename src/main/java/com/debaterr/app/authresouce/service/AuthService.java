package com.debaterr.app.authresouce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager manager;

    public AuthService(AuthenticationManager manager) {
        this.manager = manager;
    }

    public void verifyUserLogin(String username, String password) {
        Authentication authenticate = manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
    public void signup() {}
}
