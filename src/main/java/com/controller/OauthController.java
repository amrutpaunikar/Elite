package com.controller;
import java.io.IOException;
import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class OauthController {
    
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/googlelogin")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/home")
public String home() {
    return "home";
}
}