package com.precapstone.fiveguys_backend.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 복합키를 명시하기 위한 클래스이다.
 *
 */
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ContactId {
    // Group.groupId 외래키 참조
    @Column(name = "group_id")
    private Long groupId;

    // Member.memberId 외래키 참조
    @Column(name = "member_id")
    private Long member_id;

    /**
     *
     * @param groupId Group.groupId
     * @param member_id Member.memberId
     */
    public ContactId(Long groupId, Long member_id) {
        this.groupId = groupId;
        this.member_id = member_id;
    }
}
