package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.entity.Member;
import com.precapstone.fiveguys_backend.group.Group;
import jakarta.persistence.*;
import lombok.*;

/**
 * 그룹에 속한 유저의 별명과 전화번호를 연결하는 엔티티이다.
 *
 * 외래키 group_id와 member_id로 구성된 복합키로 식별된다.
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
    // group_id와 member_id로 구성된 복합키이다.
    @EmbeddedId
    private ContactId contactId;

    // 소속된 그룹
    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    // 유저
    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 그룹 내 명칭
    @Column(nullable = false)
    private String name;

    // 사용자 연락처
    @Column(nullable = false)
    private String telNum;
}
