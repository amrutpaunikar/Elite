package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OauthController {
    
    

    @GetMapping("/googlelogin")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }

}