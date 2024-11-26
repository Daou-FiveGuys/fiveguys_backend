package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    private String name;
    @Nullable
    @Setter
    private String password;
    private String provider;
    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private AmountUsed amountUsed;
}
