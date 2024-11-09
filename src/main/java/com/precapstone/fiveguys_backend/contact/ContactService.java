package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final GroupService groupService;
    private final ContactRepository contactRepository;

    /**
     * 주소록을 생성하는 함수
     *
     * @param contactCreateDTO 주소록 생성 정보
     * @return
     */
    public Contact createContact(ContactCreateDTO contactCreateDTO) {
        // 그룹ID를 통해 추가할 그룹의 정보를 조회한다.
        var groups = groupService.infoById(contactCreateDTO.getContactId().getGroupsId());

        // 주소록 생성
        var contact = Contact.builder()
                .groups(groups)
                .name(contactCreateDTO.getName())
                .telNum(contactCreateDTO.getTelNum())
                .build();

        // 주소록 저장
        contactRepository.save(contact);

        return contact;
    }

    /**
     * 그룹 명과 그룹 내 명칭을 통해, 특정 주소록의 정보를 조회하는 함수
     *
     * @param groupName 등록된 그룹의 이름
     * @param name 조회할 그룹 내 명칭
     * @return
     */
    public Contact infoByGroupAndName(String groupName, String name) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);

        // 그룹과 그룹 내 명칭을 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndName(group, name)
                .orElseThrow(() -> new RuntimeException("Group And Name Not Found"));

        return contact;
    }

    /**
     * 그룹 명과 연락처를 통해, 특정 주소록의 정보를 조회하는 함수
     *
     * @param groupName 등록된 그룹의 이름
     * @param telNum 조회할 연락처
     * @return
     */
    public Contact infoByGroupsAndTelNum(String groupName, String telNum) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);
        
        // 그룹과 연락처를 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndTelNum(group, telNum)
                .orElseThrow(() -> new RuntimeException("Group And TelNum Not Found"));

        return contact;
    }

    /**
     * 그룹명을 통해, 그룹 내 모든 주소록을 조회한다.
     *
     * @param groupName 조회할 그룹명
     * @return
     */
    public List<Contact> contactsInGroup(String groupName) {
        var group = groupService.infoByName(groupName);
        var contacts = contactRepository.findByGroups(group)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return contacts;
    }

    /**
     * 주소록을 삭제하는 함수
     *
     * @param contactId 삭제할 주소록ID(GroupId, UserId)
     * @return
     */
    public Contact deleteContact(ContactId contactId) {
        var contact = contactRepository.findByContactId(contactId)
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        contactRepository.deleteByContactId(contactId);

        return contact;
    }

    /**
     * 주소록을 수정하는 함수
     * 
     * @param contactPatchDTO 주소록 변경 정보
     * @return
     */
    public Contact updateContact(ContactPatchDTO contactPatchDTO) {
        var contact = contactRepository.findByContactId(contactPatchDTO.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact Not Found"));

        // TODO: 테스트해 볼 것
        // 특정 인자가 null로 반환된 경우 수정정보가 저장되지 않는다.
        if(contactPatchDTO.getNewName() != null) contact.setName(contactPatchDTO.getNewName());
        if(contactPatchDTO.getNewTelNum() != null) contact.setTelNum(contactPatchDTO.getNewTelNum());

        contactRepository.save(contact);

        return contact;
    }
}
