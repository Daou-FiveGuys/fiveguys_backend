package com.precapstone.fiveguys_backend.api.service;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    @Autowired
    private final TemplateEngine templateEngine;
    private final String sender = "daou.fiveguys@gmail.com";
    private final RedisService redisService;

    /**
     * 인증로직
     * Redis 조회 -> 인증번호 비교 -> 결과 리턴
     *
     * @param toEmail 수신자 이메일
     * @param userCode 입력된 인증 코드
     * @return CommonResponse
     */
    public CommonResponse verifyCode(String toEmail, String userCode) {
        String verifyCode = redisService.get(toEmail);
        if(verifyCode == null) {
            //TODO 예외처리
            /**
             * redis에 이메일에 해당하는 데이터 없음
             */
            return CommonResponse.builder()
                    .code(200)
                    .message("Verification Data Does Not Exist")
                    .data(false)
                    .build();
        }
        return CommonResponse.builder()
                    .code(200)
                    .message("Verification Data Exist")
                    .data(verifyCode.equals(userCode))
                    .build();
    }

    /**
     * 메일 발송
     * 매개변수로 초기화된 이메일 발송
     *
     * @param toEmail 수신자 이메일
     * @param title 메일 제목
     * @param text 메일 내용
     * @return CommonResponse
     * @throws MessagingException
     */
    public CommonResponse sendEmail(String toEmail, String title, String text) throws MessagingException {
        MimeMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            mailSender.send(emailForm);
            return CommonResponse.builder()
                    .code(200)
                    .message("Email Sent Successfully")
                    .data(emailForm)
                    .build();
        } catch (MailException e) {
            log.error("Error while sending email", e);
            return CommonResponse.builder()
                    .code(500)
                    .message("Email Sending Failed")
                    .build();
        }
    }

    /**
     * 인증 이메일 발송
     * Redis 확인 -> 인증 번호 생성 -> 메일 발송 -> 결과 리턴
     *
     * @param toEmail 수신자 이메일
     * @return CommonResponse
     * @throws MessagingException
     */
    public CommonResponse sendVerificationEmail(String toEmail) throws MessagingException {
        /**
         * 전송된 이메일이 Redis에 존재 -> 삭제
         */
        if(redisService.exists(toEmail))
            redisService.delete(toEmail);
        String authNumber = createVerificationNumber();
        String body = createVerificationMessage(authNumber);
        redisService.setDataExpire(toEmail, authNumber,60 * 30L);
        return sendEmail(toEmail, "FiveGuys 이메일 인증", body); // CommonResponse
    }

    private SimpleMailMessage createSimpleEmailForm(String toEmail, String title, String body) {
        SimpleMailMessage emailForm = new SimpleMailMessage();
        emailForm.setFrom(sender);
        emailForm.setTo(toEmail);
        emailForm.setSubject(title);
        emailForm.setText(body);
        return emailForm;
    }

    private MimeMessage createEmailForm(String toEmail, String title, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(sender);
        message.setRecipients(MimeMessage.RecipientType.TO, toEmail);
        message.setSubject(title);
        message.setText(body, "UTF-8", "html");
        return message;
    }

    private String createVerificationMessage(String authNumber){
        Context context = new Context();
        context.setVariable("authNumber", authNumber);
        return templateEngine.process("email_verification", context);
    }

    public static String createVerificationNumber(){
        Random random = new Random();
        StringBuilder number = new StringBuilder();
        for(int i = 0 ; i < 6 ; i ++)
            number.append(random.nextInt(10));
        return number.toString();
    }

}
