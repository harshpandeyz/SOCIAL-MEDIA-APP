package com.studentlink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studentlink.dto.AuthResponse;
import com.studentlink.dto.LoginRequest;
import com.studentlink.dto.RegisterRequest;
import com.studentlink.service.AuthService;
import com.studentlink.dto.VerifyOtpRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // ✅ Manual constructor (no Lombok)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 🔹 REGISTER API
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // 🔹 LOGIN API
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }
}