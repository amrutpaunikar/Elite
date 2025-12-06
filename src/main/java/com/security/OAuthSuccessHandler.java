package com.security;

import com.model.GoogleLoginStats;
import com.repository.GoogleLoginStatsRepository;
import com.service.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private GoogleLoginStatsRepository repo;
    @Override
public void onAuthenticationSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication)
        throws IOException, ServletException {

    OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuthUser = oauth.getPrincipal(); // FIXED

    String email = oAuthUser.getAttribute("email");
    String name = oAuthUser.getAttribute("name");
    String picture = oAuthUser.getAttribute("picture");

    // Google has no "username" attribute
    String username = email != null ? email.split("@")[0] : name;

    // Generate JWT
    String token = jwtUtils.generateToken(email, name);

    // Set cookie
    Cookie cookie = new Cookie("jwtToken", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60);
    cookie.setAttribute("SameSite", "None");
    response.addCookie(cookie);

    // Save login record
    repo.save(new GoogleLoginStats(email, username, name, picture, new Date()));

    System.out.println("Generated JWT Token by Google = " + token);

    response.sendRedirect("/");
}

    
}
