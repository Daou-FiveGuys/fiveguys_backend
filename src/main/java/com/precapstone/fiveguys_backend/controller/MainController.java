package com.precapstone.fiveguys_backend.controller;

import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.member.Member;
import com.precapstone.fiveguys_backend.member.MemberRepository;
import com.precapstone.fiveguys_backend.member.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final MemberService memberService;

    public MainController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String index() {
        return "index";  // index.html 렌더링
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // login.html 렌더링
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";  // signup.html 렌더링
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        try {
            Member member = memberService.login("fiveguys_" + email, password);
            if (!member.getEmailVerified()) {
                model.addAttribute("error", "이메일 인증이 필요합니다.");
                return "login";
            }
            return "redirect:/index";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
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

        memberService.register(email, name, password);

        return "redirect:/login";
    }
}
