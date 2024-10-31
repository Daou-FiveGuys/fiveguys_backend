package com.precapstone.fiveguys_backend.message.auth;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PpurioAuth {
    private RestTemplate restTemplate;

    String url = "https://message.ppurio.com";
    String ppurioAuthorization = Dotenv.load().get("PPURIO_AUTHORIZATION");

    public String createPost() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic"+encode(ppurioAuthorization));

        String requestBody = "";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url+"/v1/token", request, String.class);
    }

    public static String encode(String input) {
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);
    }

}
