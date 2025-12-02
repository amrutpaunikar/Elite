package com.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    @GetMapping("/logout-google")
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    
        // Redirect to Google logout page, then back to your home
        response.sendRedirect("https://accounts.google.com/Logout?continue=https://google.com");
    }
    
}
