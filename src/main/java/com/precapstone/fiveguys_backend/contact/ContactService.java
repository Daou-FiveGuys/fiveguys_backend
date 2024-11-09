package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final GroupService groupService;
    private final UserService userService;
    private final ContactRepository contactRepository;

    public Contact createContact(ContactCreateParam contactCreateParam) {
        var groups = groupService.infoById(contactCreateParam.getGroupsId());
        var user = userService.findByUserId(String.valueOf(contactCreateParam.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var contact = Contact.builder()
                .contactId(new ContactId(groups.getGroupsId(),user.getId()))
                .groups(groups)
                .user(user)
                .name(contactCreateParam.getName())
                .telNum(contactCreateParam.getTelNum())
                .build();

        contactRepository.save(contact);
        return contact;
    }

    public Contact infoByGroupAndName(String groupName, String name) {
        var group = groupService.infoByName(groupName);
        var contact = contactRepository.findByGroupsAndName(group, name)
                .orElseThrow(() -> new RuntimeException("Group And Name Not Found"));

        return contact;
    }

    public Contact infoByGroupsAndTelNum(String groupName, String telNum) {
        var group = groupService.infoByName(groupName);
        var contact = contactRepository.findByGroupsAndTelNum(group, telNum)
                .orElseThrow(() -> new RuntimeException("Group And TelNum Not Found"));

        return contact;
    }

    public List<Contact> contactsInGroup(String groupName) {
        var group = groupService.infoByName(groupName);
        var contacts = contactRepository.findByGroups(group)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return contacts;
    }

    public Contact deleteContact(Long contactId) {
        var contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        contactRepository.deleteById(contactId);

        return contact;
    }

    public Contact updateContact(ContactPatchParam contactPatchParam) {
        var contact = contactRepository.findByContactId(contactPatchParam.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        if(contactPatchParam.getNewName() != null) contact.setName(contactPatchParam.getNewName());
        if(contactPatchParam.getNewTelNum() != null) contact.setTelNum(contactPatchParam.getNewTelNum());

        contactRepository.save(contact);

        return contact;
    }
}
