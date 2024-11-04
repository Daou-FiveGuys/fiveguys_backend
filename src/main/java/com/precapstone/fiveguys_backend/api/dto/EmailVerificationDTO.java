package com.precapstone.fiveguys_backend.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmailVerificationDTO {
    private String email;
    private String code;
}
