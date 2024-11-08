package com.precapstone.fiveguys_backend.contact;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactCreateParam {
    private int memberId;
    private int groupsId;
    private String name;
    private String telNum;
}
