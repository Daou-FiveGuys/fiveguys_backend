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

    /**
     * 일반 로그인
     * @param loginInfoDTO 이메일, 비밀번호
     * @return ResponseEntity 응답
     */
    @PostMapping
    public ResponseEntity<CommonResponse> fiveguyLogin(@RequestBody LoginInfoDTO loginInfoDTO) {
        return ResponseEntity.ok(authService.signIn(loginInfoDTO));
    }

    /**
     * 네이버 소셜 로그인
     * @param code 소셜 로그인 코드
     * @param state CSRF를 방지하기 위한 인증값
     * @return ResponseEntity 응답
     */
    @GetMapping("/naver")
    public ResponseEntity<CommonResponse> naverLogin(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(authService.signIn(code, LoginType.NAVER));
    }

    /**
     * 구글 소셜 로그인
     * @param code 소셜 로그인 코드
     * @return ResponseEntity 응답
     */
    @GetMapping("/google")
    public ResponseEntity<CommonResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(authService.signIn(code, LoginType.GOOGLE));
    }

    /**
     * 액세스 토큰 재발급
     * @param authorization 액세스 토큰
     * @return ResponseEntity 응답
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse> refreshToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(authService.refreshAccessToken(accessToken));
    }

    /**
     * 로그아웃
     * @param authorization 액세스 토큰
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(authService.logout(accessToken));
    }
}
