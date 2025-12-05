package com.security;

import com.model.GoogleLoginStats;
import com.model.User;
import com.repository.GoogleLoginStatsRepository;
import com.service.JwtUtils;
import com.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private GoogleLoginStatsRepository repo;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
            throws IOException, jakarta.servlet.ServletException {
    
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> userAttributes = oauth.getPrincipal().getAttributes();
    
        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.get("name");
        String picture = (String) userAttributes.get("picture");
    
        // Save login event to MongoDB
        repo.save(new GoogleLoginStats(email, name, picture, new Date()));
    
        // Find or create user in database
        Optional<User> existingUser = userService.findByEmail(email);
        User user;
        
        if (existingUser.isEmpty()) {
            // Create new user for Google OAuth (no password needed)
            user = new User(email, ""); // Empty password for OAuth users
            user = userService.save(user);
        } else {
            user = existingUser.get();
        }
    
        // Generate JWT token
        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        
        // Store token in session so it can be retrieved later
        request.getSession().setAttribute("oauth_token", token);
        request.getSession().setAttribute("oauth_user_id", user.getId());
        request.getSession().setAttribute("oauth_user_email", user.getEmail());


        // Redirect to dashboard with token in URL (or you can use /oauth/token endpoint)
        // Option 1: Redirect with token in URL (for frontend to extract)
        String redirectUrl = "/?token=" + token;
        this.setDefaultTargetUrl(redirectUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
}
