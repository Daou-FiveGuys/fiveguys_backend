package com.precapstone.fiveguys_backend.api.contact;

import com.precapstone.fiveguys_backend.entity.Contact;
import com.precapstone.fiveguys_backend.entity.Groups;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class ContactResponse {
    private List<Contact> contacts;
    private List<Groups> childGroups;
}
