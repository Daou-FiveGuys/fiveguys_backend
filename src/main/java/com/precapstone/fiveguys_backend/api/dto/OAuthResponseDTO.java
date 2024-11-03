package com.precapstone.fiveguys_backend.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthResponseDTO {
    private final String name;
    private final String userId;
    private final String accessToken;
    private final String refreshToken;
    private final boolean isVerified;
}
