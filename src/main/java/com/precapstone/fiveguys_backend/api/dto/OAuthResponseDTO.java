package com.precapstone.fiveguys_backend.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class OAuthResponseDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Boolean isMember;
    public OAuthResponseDTO(String userId, String accessToken, String refreshToken, Boolean isMember) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isMember = isMember;
    }
}
