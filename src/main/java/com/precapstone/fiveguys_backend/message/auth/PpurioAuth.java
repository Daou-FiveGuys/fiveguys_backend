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

    String url = "https://message.ppurio.com";

    @Value("${spring.ppurio.account}")
    String ppurioAccount;

    @Value("${spring.ppurio.auth}")
    String ppurioAuthorization;

    private static AccessTocken accessTocken;

    public String createPost() {
        return getAccessToken();
    }

    public void renewAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic "+Base64.getEncoder().encodeToString((ppurioAccount + ":" + ppurioAuthorization).getBytes()));

        String requestBody = "";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        accessTocken = restTemplate.postForObject(url+"/v1/token", request, AccessTocken.class);
    }

    public synchronized String getAccessToken() {
        if(accessTocken == null
                || Long.parseLong(accessTocken.getExpired()) <= Long.parseLong(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
            renewAccessToken();

        return accessTocken.getTocken();
    }
}
