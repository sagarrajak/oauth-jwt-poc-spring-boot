package com.debaterr.app.authresouce.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthLoginRequest {
    private String username;
    private String password;
}
