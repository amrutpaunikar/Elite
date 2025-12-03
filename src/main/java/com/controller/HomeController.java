package com.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {
    
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }


    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User user) {
        return "redirect:http://localhost:5137/dashboard";
    }
}
