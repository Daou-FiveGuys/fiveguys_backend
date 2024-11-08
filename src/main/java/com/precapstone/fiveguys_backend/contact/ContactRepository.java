package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.entity.Member;
import com.precapstone.fiveguys_backend.group.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findByContactId(ContactId contactId);

    // 같은 이름의 유저가 다른 그룹에 속해있을 수 있음
    Optional<List<Contact>> findByMember(Member member);

    // 같은 그룹 내부 속성이 여러개 존재할 수 있음
    Optional<List<Contact>> findByGroups(Groups groups);

    // 다른 그룹 내부에 동일한 이름이 존재할 수 있음
    Optional<List<Contact>> findByName(String name);
    
    Optional<Contact> findByGroupsAndName(Groups groups, String name);

    Optional<Contact> findByGroupsAndTelNum(Groups groups, String telNum);

    // 같은 번호의 유저가 다른 그룹에 속해있을 수 있음
    Optional<List<Contact>> findByTelNum(String telNum);

    // TODO: Group, 전화번호를 통해 Contact 참조
}
