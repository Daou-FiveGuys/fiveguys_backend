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
@Table(name = "group2")
public class Group2 {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupsId;

    // 그룹 명칭
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder2_id")
    @JsonBackReference
    private Folder2 folder2;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group2", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Contact2> contact2s;
}
