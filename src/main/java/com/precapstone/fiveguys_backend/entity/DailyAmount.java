package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_amount")
public class DailyAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAmountId;

    // 문자 발신 총 횟수
    @Builder.Default
    @Column(nullable = false)
    private Integer msgScnt = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer msgGcnt = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer imgScnt = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer imgGcnt = 0;

    @Builder.Default
    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amount_used_id")
    @JsonBackReference
    private AmountUsed amountUsed;
}
