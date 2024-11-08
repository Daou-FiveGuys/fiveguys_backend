package com.precapstone.fiveguys_backend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
}
