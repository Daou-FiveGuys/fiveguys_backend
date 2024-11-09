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
    private Long groupsId;

    // User.userId 외래키 참조
    @Column(name = "user_id")
    private Long userId;

    /**
     *
     * @param groupsId Group.groupId
     * @param userId User.Id
     */
    public ContactId(Long groupsId, Long userId) {
        this.groupsId = groupsId;
        this.userId = userId;
    }
}
