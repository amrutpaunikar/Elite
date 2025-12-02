package com.security;

import com.model.GoogleLoginStats;
import com.repository.GoogleLoginStatsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private GoogleLoginStatsRepository repo;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
            throws IOException, jakarta.servlet.ServletException {
    
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> user = oauth.getPrincipal().getAttributes();
    
        String email = (String) user.get("email");
        String name = (String) user.get("name");
        String picture = (String) user.get("picture");
    
        // Save login event to MongoDB
        repo.save(new GoogleLoginStats(email, name, picture, new Date()));
    
        // Redirect after login
        this.setDefaultTargetUrl("/home");
    
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
}
