package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "folder2")
public class Folder2 {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;

    // 폴더 명칭
    @Column(nullable = false)
    private String name;

    // 폴더 및 하위 엔티티 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder2", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Group2> group2s;
}
