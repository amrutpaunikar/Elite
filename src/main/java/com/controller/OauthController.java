package com.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;


@RestController
public class OauthController {
    
    @GetMapping("/")
    public ResponseEntity<?> getJwt(HttpSession session) {
        String token = (String) session.getAttribute("jwt_token");

        if (token == null) {
            return ResponseEntity.status(401).body("JWT not found, please login again");
        }

        return ResponseEntity.ok(Map.of("jwt", token));
    }

    

}