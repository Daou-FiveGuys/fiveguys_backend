package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.entity.Member;
import com.precapstone.fiveguys_backend.group.Group;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "contact")
public class Contact {
    @EmbeddedId
    private ContactId contactId;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 그룹 내 명칭
    private String name;

    // 사용자 연락처
    private String telNum;
}
