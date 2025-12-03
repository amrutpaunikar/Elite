package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OauthController {
    
    

    @GetMapping("/dashboard")
    public String googleLogin() {
        return "redirect:http://localhost:5173";
    }

}