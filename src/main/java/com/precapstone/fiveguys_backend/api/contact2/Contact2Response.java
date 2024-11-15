package com.precapstone.fiveguys_backend.api.contact2;

import com.precapstone.fiveguys_backend.entity.Contact;
import com.precapstone.fiveguys_backend.entity.Groups;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class Contact2Response {
    private List<Contact> contacts;
    private List<Groups> childGroups;
}
