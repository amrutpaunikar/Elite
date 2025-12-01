package com.controller;


import com.model.User;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(org.springframework.security.core.Authentication authentication){
        if (authentication == null || authentication.getPrincipal() == null){
            return ResponseEntity.status(401).body(java.util.Map.of("error","Unauthorized"));
        }
        User user = (User) authentication.getPrincipal();
        var opt = userService.findById(user.getId());
        if (opt.isEmpty()){
            return ResponseEntity.status(404).body(java.util.Map.of("error","User not found"));
        }
        User full = opt.get();

        // Build a simple stats object
        var stats = java.util.Map.of(
                "recentSearchCount", full.getRecentSearches().size()
        );

        var resp = java.util.Map.of(
                "user", java.util.Map.of("id", full.getId(), "email", full.getEmail()),
                "stats", stats,
                "recentSearches", full.getRecentSearches()
        );

        return ResponseEntity.ok(resp);
    }
}
