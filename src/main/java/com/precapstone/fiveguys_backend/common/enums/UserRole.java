package com.precapstone.fiveguys_backend.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER"),
    VISITOR("VISITOR");
    private final String role;
    UserRole(String role) {
        this.role = role;
    }
}
