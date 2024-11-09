package com.precapstone.fiveguys_backend.contact;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactCreateParam {
    private Long userId;
    private Long groupsId;
    private String name;
    private String telNum;
}
