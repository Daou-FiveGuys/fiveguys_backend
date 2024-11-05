package com.precapstone.fiveguys_backend.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ContactId {
    @Column(name = "group_id")
    private int groupId;

    @Column(name = "member_id")
    private int member_id;

    public ContactId(int groupId, int member_id) {
        this.groupId = groupId;
        this.member_id = member_id;
    }
}
