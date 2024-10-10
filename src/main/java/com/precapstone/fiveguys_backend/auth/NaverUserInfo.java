package com.precapstone.fiveguys_backend.auth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes){
//        try {
//            this.attributes = (Map<String, Object>) attributes.get("response");
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
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

    public String getPhoneNumber(){
        return attributes.get("phoneNumber").toString();
    }
}
