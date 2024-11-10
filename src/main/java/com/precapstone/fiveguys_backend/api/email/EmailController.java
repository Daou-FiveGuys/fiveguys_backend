package com.precapstone.fiveguys_backend.api.email;

import com.precapstone.fiveguys_backend.api.dto.EmailVerificationDTO;
import com.precapstone.fiveguys_backend.api.dto.ResetPasswordDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author 6-keem
 */
@Tag(name = "Email Verification", description = "이메일 인증")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {
    private final MailService mailService;

    /**
     * 인증 메일 전송
     *
     * @param authorization 토큰
     * @return ResponseEntity<CommonResponse> 메일 전송 성공 여부
     */
    @GetMapping
    public ResponseEntity<CommonResponse> mailSend(@RequestHeader("Authorization") String authorization) {
        try {
            String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
            return ResponseEntity.ok(mailService.sendEmailWithAccessToken(accessToken));
        } catch (Exception exception) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            return ResponseEntity.ok(mailService.resetPassword(resetPasswordDTO));
        } catch (Exception exception) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }

    /**
     * 인증 번호 검증
     *
     * @param emailVerificationDTO 이메일, 인증코드 클래스
     * @return ResponseEntity<CommonResponse> 인증 성공 여부
     */
    @PostMapping
    public ResponseEntity<CommonResponse> verify(@RequestHeader("Authorization") String authorization, @RequestBody EmailVerificationDTO emailVerificationDTO){
        try {
            String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
            String code = emailVerificationDTO.getCode();
            return ResponseEntity.ok(mailService.verifyCode(accessToken, code));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }
}
