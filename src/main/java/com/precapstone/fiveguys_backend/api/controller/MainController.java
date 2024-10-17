package com.precapstone.fiveguys_backend.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";  // signup.html 렌더링
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/verification")
    public String verification(HttpServletRequest request, HttpSession session, Model model) {
        if(!request.isUserInRole("ROLE_VISITOR")){
            return "redirect:/";
        }
        String email = session.getAttribute("email").toString();
        model.addAttribute("email", email);
        return "verification";
    }


    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "login";
    }
}
