package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.model.GoogleLoginStats;
import com.repository.GoogleLoginStatsRepository;

@RestController
@RequestMapping("/admin")
public class AnalyticsController {

    @Autowired
    private GoogleLoginStatsRepository repo;

    @GetMapping("/google-logins")
    public List<GoogleLoginStats> getGoogleLogins() {
        return repo.findAll();
    }

    @GetMapping("/google-login-count")
    public long getLoginCount() {
        return repo.count();
    }
}
