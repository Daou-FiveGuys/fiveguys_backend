package com.precapstone.fiveguys_backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 그룹에 속한 유저의 별명과 전화번호를 연결하는 엔티티이다.
 *
 * 외래키 group_id와 user_id로 구성된 복합키로 식별된다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "contact")
public class Contact {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups_id")
    private Groups groups;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 그룹 내 명칭
    @Column(nullable = false)
    private String name;

    // 사용자 연락처
    @Column(nullable = false)
    private String telNum;
}
