package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    // 문자 발신 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer msgScnt = 0;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy= "amountUsed", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DailyAmount> dailyAmounts;

    // 마지막 이미지 생성 일자
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime lastDate = LocalDateTime.now();
}
