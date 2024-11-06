package com.precapstone.fiveguys_backend.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private final String accessToken;
}
