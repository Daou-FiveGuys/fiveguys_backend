package com.precapstone.fiveguys_backend.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    private String memberId;
    private String requestId;
    @Column(columnDefinition = "TEXT")
    private String originalImageLink;
    @Nullable
    private String editedImageLink;
}
