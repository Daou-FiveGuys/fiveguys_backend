package com.precapstone.fiveguys_backend.auth;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getProviderEmail();
    String getName();
}