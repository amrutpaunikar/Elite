package com.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model.Product;
import com.model.User;
import com.repository.ProductRepository;
import com.service.UserService;

@RestController
public class SearchController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name="q", required=false) String q,
                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authHeader){
        if (q == null || q.trim().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error","Missing query parameter 'q'"));
        }

        String regex = ".*" + java.util.regex.Pattern.quote(q) + ".*";
        List<Product> found = productRepository.findByTitleRegexIgnoreCase(regex);

        // If the call has a Bearer token, store the search into user's recentSearches
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // JwtAuthFilter would normally set the authentication; here we try to fetch user by token optionally
            // To keep things simple we check SecurityContext
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                User u = (User) auth.getPrincipal();
                Optional<User> opt = userService.findById(u.getId());
                if (opt.isPresent()){
                    User user = opt.get();
                    var list = user.getRecentSearches();
                    list.add(0, q);
                    if (list.size() > 10) list.remove(list.size() - 1);
                    userService.save(user);
                }
            }
        }

        // Build response
        var results = found.stream().map(p ->
                Map.of(
                    "id", p.getId(),
                    "title", p.getTitle(),
                    "description", p.getDescription()
              
                )
        ).toList();

        var response = Map.of(
                "total", results.size(),
                "results", results
        );

        return ResponseEntity.ok(response);
    }
}

