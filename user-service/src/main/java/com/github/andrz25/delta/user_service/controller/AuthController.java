package com.github.andrz25.delta.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.andrz25.delta.user_service.request.LoginRequest;
import com.github.andrz25.delta.user_service.response.LoginResponse;
import com.github.andrz25.delta.user_service.security.JwtService;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        Authentication authResult = authenticationManager.authenticate(authToken);

        String token = jwtService.generateToken(authResult);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
