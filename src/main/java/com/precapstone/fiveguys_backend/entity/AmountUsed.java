package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "amount_used")
public class AmountUsed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amountUsedId;

    // 유저 정보
    @JsonBackReference
    @OneToOne(mappedBy = "amountUsed", cascade = CascadeType.ALL)
    private User user;

    // 문자 발신 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer msgScnt = 0;
    
    // 문자 생성 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer msgGcnt = 0;
    
    // 이미지 발신 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer imgScnt = 0;
    
    // 이미지 생성 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer imgGcnt = 0;

    // 일 이미지 생성 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer imgDcnt = 0;

    // 마지막 이미지 생성 일자
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime imgDate = LocalDateTime.now();
}
