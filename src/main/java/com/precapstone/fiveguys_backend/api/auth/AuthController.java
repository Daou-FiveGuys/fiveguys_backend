package com.precapstone.fiveguys_backend.api.auth;

import com.precapstone.fiveguys_backend.api.dto.LoginInfoDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import com.precapstone.fiveguys_backend.common.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/isVerified")
    public ResponseEntity<CommonResponse> isVerified(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(authService.isVerified(accessToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse> refreshToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(authService.refreshAccessToken(accessToken));
    }

    @GetMapping("/naver")
    public ResponseEntity<CommonResponse> naverLogin(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(authService.signIn(code, LoginType.NAVER));
    }

    @GetMapping("/google")
    public ResponseEntity<CommonResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(authService.signIn(code, LoginType.GOOGLE));
    }

    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestParam String userId) {
        return ResponseEntity.ok(authService.logout(userId));
    }

    @PostMapping
    public ResponseEntity<CommonResponse> fiveguyLogin(@RequestBody LoginInfoDTO loginInfoDTO) {
        return ResponseEntity.ok(authService.signIn(loginInfoDTO));
    }
}
