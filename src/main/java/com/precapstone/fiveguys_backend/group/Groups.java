package com.precapstone.fiveguys_backend.group;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 각 연락처를 묶는 상위 그룹이다.
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
    private int groupsId;

    // 그룹의 이름 ※ 동일 이름을 구분할 식별자(대표 유저 등)가 없기 때문에, Unique로 설정
    @Column(unique = true, nullable = false)
    private String groupsName;

    // 소속된 그룹, 없다면 Null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "groupsId")
    private Groups parent;

    // 소속된 그룹, 없다면 Null
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Groups> child;
}
