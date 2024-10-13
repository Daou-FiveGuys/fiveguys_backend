package com.precapstone.fiveguys_backend.entity;

import com.precapstone.fiveguys_backend.common.enums.UserRole;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    private String name;
    @Nullable
    private String password;
    private String provider;
    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Setter
    private Boolean emailVerified = false;
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
}
