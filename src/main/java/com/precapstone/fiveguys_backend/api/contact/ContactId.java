package com.precapstone.fiveguys_backend.api.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 복합키를 명시하기 위한 클래스이다.
 *
 */
@Setter
@Getter
@Builder
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

    public ContactId(Long groupsId, Long userId) {
        this.groupsId = groupsId;
        this.userId = userId;
    }
}
