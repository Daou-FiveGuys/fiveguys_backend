package com.precapstone.fiveguys_backend.auth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getProviderName() {
        return attributes.get("name").toString();
    }
}
