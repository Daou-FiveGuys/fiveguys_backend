package com.precapstone.fiveguys_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String userProfile(Model model) {
        model.addAttribute("message", "This is your profile page!");
        return "profile";  // profile.html 렌더링
    }
}
