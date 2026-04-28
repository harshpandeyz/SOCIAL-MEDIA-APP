package com.studentlink.service;

import com.studentlink.dto.VerifyOtpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studentlink.dto.LoginRequest;
import com.studentlink.dto.RegisterRequest;
import com.studentlink.dto.AuthResponse;
import com.studentlink.model.User;
import com.studentlink.model.Role;
import com.studentlink.repository.UserRepository;
import com.studentlink.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // 🔹 REGISTER
    public AuthResponse register(RegisterRequest request) {

        // ✅ Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // ✅ Basic validation
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // 🔐 Generate OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setOtp(otp);
        user.setEmailVerified(false);

        // 📧 Send OTP FIRST (important)
        emailService.sendOtpEmail(user.getEmail(), otp);

        // 💾 Save only if email sending succeeds
        userRepository.save(user);

        return new AuthResponse("OTP sent to email");
    }

    // 🔹 LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Please verify your email first");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    // 🔹 VERIFY OTP
    public AuthResponse verifyOtp(VerifyOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getOtp() == null) {
            throw new IllegalArgumentException("OTP expired or already used");
        }

        if (!user.getOtp().equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}