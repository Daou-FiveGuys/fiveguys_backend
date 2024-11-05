package com.precapstone.fiveguys_backend.api.email;

import com.precapstone.fiveguys_backend.api.dto.EmailDTO;
import com.precapstone.fiveguys_backend.api.dto.EmailVerificationDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private final MailService mailService;

    /**
     * 이메일 전송 컨트롤러
     *
     * @param emailDTO 수취자 이메일
     * @return ResponseEntity<CommonResponse> 메일 전송 성공 여부
     * @throws MessagingException
     */
    @PostMapping("/send")
    public ResponseEntity<CommonResponse> mailSend(@RequestBody EmailDTO emailDTO) throws MessagingException {
        try {
            String email = emailDTO.getEmail();
            return ResponseEntity.ok(mailService.sendVerificationEmail(email));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }

    /**
     * 이메일 전송 컨트롤러
     *
     * @param authorization 토큰
     * @return ResponseEntity<CommonResponse> 메일 전송 성공 여부
     * @throws MessagingException
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

    /**
     * @param emailVerificationDTO 이메일, 인증코드 클래스
     * @return ResponseEntity<CommonResponse> 인증 성공 여부
     */
    //TODO redirect -> client 단에서 수행
    @PostMapping("/verify")
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
