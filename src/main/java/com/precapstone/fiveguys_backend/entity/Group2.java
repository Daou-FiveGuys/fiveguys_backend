package com.precapstone.fiveguys_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group2")
public class Group2 {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder2_id")
    private Folder2 folder2;

    // 그룹 명칭
    @Column(nullable = false)
    private String name;
}
