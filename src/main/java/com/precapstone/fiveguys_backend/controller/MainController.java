package com.precapstone.fiveguys_backend.controller;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import com.precapstone.fiveguys_backend.member.UserRepository;
import com.precapstone.fiveguys_backend.member.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final UserService userService;

    public MainController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";  // index.html 렌더링
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";  // signup.html 렌더링
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String email,
                         @RequestParam String name,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }

        if (!PasswordValidator.isValidPassword(password)) {
            model.addAttribute("error", "Invalid password format");
            return "signup";
        }

        userService.register(email, name, password);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/profile";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        try {
            CommonResponse response = userService.login("fiveguys_" + email, password);
            switch (response.getMessage()){
                case ResponseMessage.SUCCESS -> {
                    return "redirect:/profile";
                }
                case ResponseMessage.EMAIL_VERIFICAITION_REQUIRED -> {
                    return "redirect:/email_verification";
                }
                default -> {
                    return "redirect:/login";
                }
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }
}
