package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.dto.LoginInfoDTO;
import com.precapstone.fiveguys_backend.api.service.OAuthService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class LoginController {
    private final OAuthService OAuthService;

    @GetMapping("/naver")
    public ResponseEntity<CommonResponse> naverLogin(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(OAuthService.signIn(code, LoginType.NAVER));
    }

    @GetMapping("/google")
    public ResponseEntity<CommonResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(OAuthService.signIn(code, LoginType.GOOGLE));
    }

    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestParam String userId) {
        return ResponseEntity.ok(OAuthService.logout(userId));
    }

    @PostMapping
    public ResponseEntity<CommonResponse> fiveguyLogin(@RequestBody LoginInfoDTO loginInfoDTO) {
        return ResponseEntity.ok(OAuthService.signIn(loginInfoDTO));
    }
}
