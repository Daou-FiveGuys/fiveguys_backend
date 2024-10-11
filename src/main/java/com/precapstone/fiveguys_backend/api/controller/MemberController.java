package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.service.MemberService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import com.precapstone.fiveguys_backend.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

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

        memberService.register(email, name, password);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login-process")
    public String loginForm(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            CommonResponse response = memberService.login("fiveguys_" + email, password);
            switch (response.getMessage()){
                case ResponseMessage.SUCCESS -> {
                    return "redirect:/";
                }
                case ResponseMessage.EMAIL_VERIFICAITION_REQUIRED -> {
                    Member member = (Member)response.getData();
                    session.setAttribute("userId", member.getUserId());
                    session.setAttribute("name", member.getName());
                    session.setAttribute("email", member.getEmail());
                    return "redirect:/verification";
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
    
    //TODO User Role 설정해서 !(google || naver || verified) -> 진입 시키기
    @GetMapping("/verification")
    public String verification(HttpServletRequest request, HttpSession session, Model model) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        String email = session.getAttribute("email").toString();
        model.addAttribute("email", email);
        return "verification";
    }
}
