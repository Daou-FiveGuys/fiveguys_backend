package com.precapstone.fiveguys_backend.api.contact2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Contact2CreateDTO {
    private Long group2Id;
    private String name;
    private String telNum;
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;
    private String six;
    private String seven;
    private String eight;
}
