package com.precapstone.fiveguys_backend.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 복합키를 명시하기 위한 클래스이다.
 *
 */
@Setter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ContactId {
    // Group.groupId 외래키 참조
    @Column(name = "groups_id")
    private Integer groupsId;

    // Member.memberId 외래키 참조
    @Column(name = "member_id")
    private Integer memberId;

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
