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
    @Column(name = "groups_id")
    private int groupsId;

    // Member.memberId 외래키 참조
    @Column(name = "member_id")
    private int memberId;

    /**
     *
     * @param groupsId Group.groupId
     * @param memberId Member.memberId
     */
    public ContactId(int groupsId, int memberId) {
        this.groupsId = groupsId;
        this.memberId = memberId;
    }
}
