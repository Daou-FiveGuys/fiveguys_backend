package com.precapstone.fiveguys_backend.message.auth;

import lombok.Getter;

@Getter
public class AccessTocken {
    private final String tocken;
    private final String type;
    private final String expired;

    public AccessTocken(String token, String type, String expired) {
        this.tocken = token;
        this.type = type;
        this.expired = expired;
    }
}