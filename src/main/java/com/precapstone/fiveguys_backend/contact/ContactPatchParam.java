package com.precapstone.fiveguys_backend.contact;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactPatchParam {
    private int contactId;
    private String newName;
    private String newTelNum;
}
