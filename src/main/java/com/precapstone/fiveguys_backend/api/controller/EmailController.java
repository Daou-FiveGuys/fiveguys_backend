package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.service.MailService;
import com.precapstone.fiveguys_backend.api.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {
    private final MailService mailService;
    private final MemberService memberService;

    @PostMapping("/send")
    public ResponseEntity<String> mailSend(@RequestParam("email") String email) throws MessagingException {
        mailService.sendVerificationEmail(email);
        return ResponseEntity.ok("Email sent successfully");
    }

    // 인증코드 인증
    @PostMapping("/verify")
    public String verify(@RequestParam("email") String email,
                         @RequestParam("code") String code) {
        boolean isVerify = mailService.verifyCode(email, code);
        if (isVerify){
            memberService.verifiedEmail(email);
            return "redirect:/";
        } else {
            //TODO 이메일 인증 실패 시 처리
            return "redirect:/error";
        }
    }
}
