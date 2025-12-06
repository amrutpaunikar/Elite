package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dto.AuthRequest;
import com.dto.LoginReq;
import com.model.User;
import com.service.JwtUtils;
import com.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // ✅ SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Validated @RequestBody AuthRequest req,
            HttpServletResponse response) {

        Optional<User> existing = userService.findByEmail(req.getEmail());

        if (existing.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", "Email already in use"));
        }

        User user = userService.createUser(
                req.getEmail(),
                req.getPassword(),
                req.getUsername()
        );

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        setJwtCookie(response, token);

        return ResponseEntity.ok(
                java.util.Map.of("message", "Signup successful")
        );
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Validated @RequestBody LoginReq req,
            HttpServletResponse response) {

        Optional<User> userOpt = userService.findByEmail(req.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Invalid credentials"));
        }

        User user = userOpt.get();

        if (!userService.checkPassword(user, req.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        setJwtCookie(response, token);

        return ResponseEntity.ok(
                java.util.Map.of("message", "Login successful")
        );
    }

    // ✅ CENTRALIZED COOKIE CREATION (USED ONLY HERE)
    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        // ✅ DEV vs PROD handling
        cookie.setSecure(false); // true only on HTTPS
        cookie.setAttribute("SameSite", "Lax"); // Use "None" only for cross-site OAuth

        response.addCookie(cookie);
    }
}
