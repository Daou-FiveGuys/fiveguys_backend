package com.precapstone.fiveguys_backend.api.controller;

import com.precapstone.fiveguys_backend.api.dto.EmailDto;
import com.precapstone.fiveguys_backend.api.dto.EmailVerificationDto;
import com.precapstone.fiveguys_backend.api.service.MailService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
     * @param emailDto 수취자 이메일
     * @return ResponseEntity<CommonResponse> 메일 전송 성공 여부
     * @throws MessagingException
     */
    @PostMapping("/send")
    public ResponseEntity<CommonResponse> mailSend(@RequestBody EmailDto emailDto) throws MessagingException {
        try {
            String email = emailDto.getEmail();
            return ResponseEntity.ok(mailService.sendVerificationEmail(email));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }

    /**
     * @param emailVerificationDto 이메일, 인증코드 클래스
     * @return ResponseEntity<CommonResponse> 인증 성공 여부
     */
    //TODO redirect -> client 단에서 수행
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse> verify(@RequestBody EmailVerificationDto emailVerificationDto){
        try {
            String email = emailVerificationDto.getEmail();
            String code = emailVerificationDto.getCode();
            return ResponseEntity.ok(mailService.verifyCode(email,code));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .code(400)
                    .message("bad request")
                    .build());
        }
    }
}
