package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact2")
public class Contact2 {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group2_id")
    @JsonBackReference
    private Group2 group2;

    // 주소록 명칭
    @Column(nullable = false)
    private String name;

    // 주소록 전화번호
    @Column(nullable = false)
    private String telNum;

    // [*1*] 대체 단어
    @Column(nullable = false)
    private String one;

    // [*2*] 대체 단어
    @Column(nullable = false)
    private String two;

    // [*3*] 대체 단어
    @Column(nullable = false)
    private String three;

    // [*4*] 대체 단어
    @Column(nullable = false)
    private String four;

    // [*5*] 대체 단어
    @Column(nullable = false)
    private String five;

    // [*6*] 대체 단어
    @Column(nullable = false)
    private String six;

    // [*7*] 대체 단어
    @Column(nullable = false)
    private String seven;

    // [*8*] 대체 단어
    @Column(nullable = false)
    private String eight;
}
