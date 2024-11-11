package com.precapstone.fiveguys_backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 연락처들을 소유하는 엔티티이다.
 *
 * 각 그룹은 다른 그룹에 속하거나 포함될 수 있다.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
public class Groups {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupsId;

    // 소유자, 토큰인증을 위함
    @Column(nullable = false)
    private String userId;

    // 그룹의 이름
    // ※ 동일 이름을 구분할 식별자(대표 유저 등)가 없기 때문에, Unique로 설정
    @Column(unique = true, nullable = false)
    private String groupsName;

    // 소속된 그룹, 없다면 Null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "groupsId")
    private Groups parent;
}
