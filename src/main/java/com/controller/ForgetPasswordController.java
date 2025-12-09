package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dto.SignupResponse;
import com.model.User;
import com.repository.UserRepository;
import com.service.EmailService;
import com.service.JwtUtils;
import com.service.OtpService;

@RestController
@RequestMapping("/auth")
public class ForgetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JwtUtils jwtUtils;

    // 1️⃣ Forgot Password → Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

       Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
          return ResponseEntity.badRequest().body("Email not registered");
        }
    
        User user = userOpt.get();

        String token = jwtUtils.generateToken( user.getId(), user.getEmail());
        SignupResponse resp = new SignupResponse(user.getId(), token);

        String otp = otpService.generateOtp();
        otpService.saveOtpToUser(user, otp);

        emailService.sendOtp(email, otp);


        return ResponseEntity.ok(resp);
    }


    // 2️⃣ Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email");
        }

         User user = userOpt.get();

        boolean isValid = otpService.verifyOtp(user, otp);

        if (!isValid) return ResponseEntity.badRequest().body("Invalid or expired OTP");

        return ResponseEntity.ok("OTP verified. You can now reset password.");
    }


    // 3️⃣ Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String newPassword) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email");
        }

         User user = userOpt.get();
         
        user.setPassword(newPassword);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully.");
    }
}
