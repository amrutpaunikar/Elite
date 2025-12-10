package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.ForgotPasswordRequest;
import com.dto.ForgotPasswordTokenResponse;
import com.dto.ResetPasswordRequest;
import com.dto.VerifyOtpRequest;
import com.model.User;
import com.repository.UserRepository;
import com.service.EmailService;
import com.service.JwtUtils;
import com.service.OtpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Allow all origins for development and production
public class ForgetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    // 1️⃣ Forgot Password → Send OTP and Generate Token
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request received for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("Forgot password failed: Email not registered: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Email not registered");
        }
    
        User user = userOpt.get();
        log.debug("User found for email: {}", request.getEmail());

        String otp = otpService.generateOtp();
        log.debug("Generated OTP for user: {}", user.getEmail());
        
        otpService.saveOtpToUser(user, otp);
        log.debug("OTP saved to user: {}", user.getEmail());

        emailService.sendOtp(request.getEmail(), otp);
        log.info("OTP sent to email: {}", request.getEmail());

        // Generate JWT token for password reset flow
        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        log.info("Password reset token generated for user: {}", user.getEmail());

        ForgotPasswordTokenResponse response = new ForgotPasswordTokenResponse(
            "OTP sent to your email. Use the token in Authorization header as Bearer token for verification.",
            token,
            jwtExpirationMs
        );

        return ResponseEntity.ok(response);
    }


    // 2️⃣ Verify OTP (Requires Bearer token in Authorization header)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request, 
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("Verify OTP request received for email: {}", request.getEmail());

        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Bearer token is required in Authorization header for OTP verification");
            return ResponseEntity.badRequest().body("Bearer token is required in Authorization header for OTP verification");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        log.debug("Token extracted from Authorization header");

        // Validate the token
        if (!jwtUtils.validateToken(token)) {
            log.warn("Token validation failed for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        
        // Extract user ID from token and verify it matches the email
        String userIdFromToken = jwtUtils.getUserIdFromToken(token);
        Optional<User> userOptFromToken = userRepository.findById(userIdFromToken);
        
        if (userOptFromToken.isEmpty() || !userOptFromToken.get().getEmail().equals(request.getEmail())) {
            log.warn("Token user mismatch for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Token does not match the provided email");
        }
        
        log.debug("Token validated successfully for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            log.warn("OTP verification failed: Invalid email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Invalid email");
        }

        User user = userOpt.get();
        log.debug("User found for email: {}", request.getEmail());

        boolean isValid = otpService.verifyOtp(user, request.getOtp());
        log.debug("OTP verification result for user {}: {}", user.getEmail(), isValid);

        if (!isValid) {
            log.warn("OTP verification failed: Invalid or expired OTP for user: {}", user.getEmail());
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        log.info("OTP verified successfully for user: {}", user.getEmail());
        return ResponseEntity.ok("OTP verified. You can now reset password.");
    }


    // 3️⃣ Reset Password (Requires Bearer token in Authorization header)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request, 
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("Reset password request received for email: {}", request.getEmail());

        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Bearer token is required in Authorization header for password reset");
            return ResponseEntity.badRequest().body("Bearer token is required in Authorization header for password reset");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        log.debug("Token extracted from Authorization header");

        // Validate the token
        if (!jwtUtils.validateToken(token)) {
            log.warn("Token validation failed for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        
        // Extract user ID from token and verify it matches the email
        String userIdFromToken = jwtUtils.getUserIdFromToken(token);
        Optional<User> userOptFromToken = userRepository.findById(userIdFromToken);
        
        if (userOptFromToken.isEmpty() || !userOptFromToken.get().getEmail().equals(request.getEmail())) {
            log.warn("Token user mismatch for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Token does not match the provided email");
        }
        
        log.debug("Token validated successfully for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            log.warn("Password reset failed: Invalid email: {}", request.getEmail());
            return ResponseEntity.badRequest().body("Invalid email");
        }

        User user = userOpt.get();
        log.debug("User found for email: {}", request.getEmail());
         
        // Hash the new password before saving
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        log.debug("New password hashed for user: {}", user.getEmail());
        
        user.setPassword(hashedPassword);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getEmail());

        return ResponseEntity.ok("Password changed successfully.");
    }
}