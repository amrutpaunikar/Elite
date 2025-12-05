package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class OauthController {
    
    @GetMapping("/googlelogin")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }

}

@RestController
class OAuthTokenController {
    
    /**
     * Get JWT token after Google OAuth login
     * Call this endpoint after successful OAuth login to retrieve your JWT token
     * 
     * @param session HTTP session containing the OAuth token
     * @return JWT token in response body
     */
    @GetMapping("/oauth/token")
    public ResponseEntity<?> getOAuthToken(HttpSession session) {
        String token = (String) session.getAttribute("oauth_token");
        String userId = (String) session.getAttribute("oauth_user_id");
        String email = (String) session.getAttribute("oauth_user_email");
        
        if (token == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "No OAuth token found. Please login with Google first."));
        }
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "userId", userId != null ? userId : "",
            "email", email != null ? email : ""
        ));
    }
    
    /**
     * Clear OAuth session data
     */
    @GetMapping("/oauth/logout")
    public ResponseEntity<?> clearOAuthSession(HttpSession session) {
        session.removeAttribute("oauth_token");
        session.removeAttribute("oauth_user_id");
        session.removeAttribute("oauth_user_email");
        session.invalidate();
        
        return ResponseEntity.ok(Map.of("message", "OAuth session cleared successfully"));
    }
}