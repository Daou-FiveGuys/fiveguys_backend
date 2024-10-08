package com.precapstone.fiveguys_backend.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String email;
    private String username;
    private String password;
    private String provider;
    private String providerId;
}
