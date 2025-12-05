package com.security;

import com.model.GoogleLoginStats;
import com.repository.GoogleLoginStatsRepository;
import com.service.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private GoogleLoginStatsRepository repo;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
            throws IOException, ServletException {
    
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuthUser =(OAuth2User) oauth.getPrincipal().getAttributes();
    
        String email = oAuthUser.getAttribute("email");
        String name = oAuthUser.getAttribute("name");
        String picture = oAuthUser.getAttribute("picture");
        
    
        // Save login event to MongoDB
        
        String token = jwtUtils.generateToken(email, name);
        // Redirect after login

        request.getSession().setAttribute("jwt_token", token);

        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ENABLE HTTPS IN PRODUCTION
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        cookie.setAttribute("SameSite", "None"); // for OAuth redirects
        response.addCookie(cookie);

        repo.save(new GoogleLoginStats(email, name, picture, new Date()));

        System.out.println("Generated JWT Token by Google = " + token);
        
        response.sendRedirect("/");
    }
    
}
