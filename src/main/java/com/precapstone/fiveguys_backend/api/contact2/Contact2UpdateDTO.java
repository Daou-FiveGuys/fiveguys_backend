package com.precapstone.fiveguys_backend.api.contact2;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Contact2UpdateDTO {
    private Long contact2Id;
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
