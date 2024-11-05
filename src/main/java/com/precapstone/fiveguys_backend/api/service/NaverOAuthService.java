package com.precapstone.fiveguys_backend.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NaverOAuthService {
    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect_uri}")
    private String redirectUri;

    public String getAccessToken(String code) {
        return "";
    }
}
