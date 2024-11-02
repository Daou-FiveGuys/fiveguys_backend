package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.service.OAuthService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController {
    private final OAuthService OAuthService;

    @GetMapping("/naver")
    public ResponseEntity<CommonResponse> naverLogin(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(OAuthService.signIn(code, LoginType.NAVER));
    }

    @GetMapping("/google")
    public ResponseEntity<CommonResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(OAuthService.signIn(code, LoginType.GOOGLE));
    }
}
