package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.service.FiveGuysOAuthService;
import com.precapstone.fiveguys_backend.api.service.GoogleOAuthService;
import com.precapstone.fiveguys_backend.api.service.NaverOAuthService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OAuthController {
    private final NaverOAuthService naverOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final FiveGuysOAuthService fiveGuysOAuthService;

    @GetMapping("/naver")
    public ResponseEntity<CommonResponse> naverLogin(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(CommonResponse.builder().build());
    }

    @GetMapping("/google")
    public ResponseEntity<CommonResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(CommonResponse.builder().build());
    }

    @GetMapping("/fiveguys")
    public ResponseEntity<CommonResponse> fiveguysLogin(@RequestParam String code) {
        return ResponseEntity.ok(CommonResponse.builder().build());
    }
}
