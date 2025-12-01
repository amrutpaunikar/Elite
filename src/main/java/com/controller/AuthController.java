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
import com.dto.AuthResponse;
import com.dto.SignupResponse;
import com.model.User;
import com.service.JwtUtils;
import com.service.UserService;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest req){
       Optional<User> existing = userService.findByEmail(req.getEmail());

       if (existing.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", "Email already in use"));
        }

        User user = userService.createUser(req.getEmail(), req.getPassword());

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());

        SignupResponse resp = new SignupResponse(user.getId(), token);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody AuthRequest req){
        Optional<User> userOpt = userService.findByEmail(req.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Invalid credentials"));
        }

        User user = userOpt.get();

        if (!userService.checkPassword(user, req.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}

