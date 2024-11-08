package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.api.service.MemberService;
import com.precapstone.fiveguys_backend.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final GroupService groupService;
    private final MemberService memberService;
    private final ContactRepository contactRepository;

    public Contact createContact(ContactCreateParam contactCreateParam) {
        var groups = groupService.infoById(contactCreateParam.getGroupId());
        var member = memberService.findByUserId(String.valueOf(contactCreateParam.getMemberId()))
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        var contact = Contact.builder()
                .groups(groups)
                .member(member)
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

    public Contact deleteContact(int contactId) {
        var contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        contactRepository.deleteById(contactId);

        return contact;
    }

    public Contact updateContact(ContactPatchParam contactPatchParam) {
        var contact = contactRepository.findById(contactPatchParam.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        if(contactPatchParam.getNewName() != null) contact.setName(contactPatchParam.getNewName());
        if(contactPatchParam.getNewTelNum() != null) contact.setTelNum(contactPatchParam.getNewTelNum());

        contactRepository.save(contact);

        return contact;
    }
}
