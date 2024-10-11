package com.precapstone.fiveguys_backend.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final String sender = "daou.fiveguys@gmail.com";
    private final RedisService redisService;

    public Boolean verifyCode(String toEmail, String userCode) {
        String verifyCode = redisService.get(toEmail);
        if(verifyCode == null) {
            //TODO 예외처리
            return false;
        }
        return verifyCode.equals(userCode);
    }

    public void sendEmail(String toEmail, String title, String text) throws MessagingException {
        MimeMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            mailSender.send(emailForm);
        } catch (MailException e) {
            throw new IllegalArgumentException("Error while sending email");
        }
    }

    public void sendVerificationEmail(String toEmail) throws MessagingException {
        if(redisService.exists(toEmail))
            redisService.delete(toEmail);
        String authNumber = createVerificationNumber();
        redisService.setDataExpire(toEmail,authNumber,60 * 30L);
        sendEmail(toEmail, "FiveGuys 이메일 인증", authNumber);
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

    public static String createVerificationNumber(){
        Random random = new Random();
        StringBuilder number = new StringBuilder();
        for(int i = 0 ; i < 6 ; i ++)
            number.append(random.nextInt(10));
        return number.toString();
    }

    private static String createVerificationMessage(String authNumber){
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authNumber + "</h1>";
        body += "<h3>감사합니다.</h3>";
        return body;
    }
}
