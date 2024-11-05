package com.precapstone.fiveguys_backend.message.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PpurioAuth {
    private final RestTemplate restTemplate;

    // 뿌리오 API 이용을 위한 고정 주소
    @Value("${spring.ppurio.base-url}")
    private String url;

    // 이용할 뿌리오 계정
    @Value("${spring.ppurio.account}")
    private String ppurioAccount;

    // 뿌리오에서 발급한 연동 개발 인증키
    @Value("${spring.ppurio.auth}")
    private String ppurioAuthorization;

    // 엑세스 토큰
    private AccessTocken accessTocken;

    /**
     * 테스트를 위한 임시 함수
     * TestControl 삭제 시 함께 삭제할 것
     * @return AccessToken 값
     */
    public String createPost() {
        return getAccessToken();
    }

    /**
     * 새로운 액세스 토큰을 생성하는 함수
     * 뿌리오 서비스에 새로운 토큰 값을 요청한다.
     */
    public void renewAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic "+Base64.getEncoder().encodeToString((ppurioAccount + ":" + ppurioAuthorization).getBytes()));

        String requestBody = "";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        accessTocken = restTemplate.postForObject(url+"/v1/token", request, AccessTocken.class);
    }

    /**
     * 액세스 토큰 값을 반환하는 함수
     * 만약 기존의 토큰이 발급되어있지 않거나, 토큰 만료 시간이 지난경우 재발급을 진행한다.
     *
     * @return 액세스 토큰 값
     */
    public synchronized String getAccessToken() {
        if(accessTocken == null
                || Long.parseLong(accessTocken.getExpired()) <= Long.parseLong(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
            renewAccessToken();

        return accessTocken.getTocken();
    }
}
