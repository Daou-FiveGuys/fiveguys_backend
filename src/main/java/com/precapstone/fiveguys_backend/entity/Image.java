package com.precapstone.fiveguys_backend.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Nullable
    private String parentRequestId;
    private String userId;
    @NotNull
    private String requestId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Image setUrl(String url) {
        this.url = url;
        return this;
    }
}
