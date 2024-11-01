package com.precapstone.fiveguys_backend.message.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PpurioAuth {
    private final RestTemplate restTemplate;

    String url = "https://message.ppurio.com";

    @Value("${spring.ppurio.auth}")
    String ppurioAuthorization;

    @Value("${spring.ppurio.account}")
    String ppurioAccount;

    public String createPost() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic "+encode(ppurioAccount+" : "+ppurioAuthorization));

        String requestBody = "";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url+"/v1/token", request, String.class);
    }

    public static String encode(String input) {
        return Base64.getEncoder().encodeToString((input).getBytes());
    }

}
