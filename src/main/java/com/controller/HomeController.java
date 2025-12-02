package com.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User user) {
        return "Logged in as: " + user.getAttribute("email");
    }
}
