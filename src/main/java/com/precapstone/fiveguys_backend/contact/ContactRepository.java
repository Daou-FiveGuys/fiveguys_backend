package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.entity.Member;
import com.precapstone.fiveguys_backend.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // 같은 이름의 유저가 다른 그룹에 속해있을 수 있음
    Optional<List<Contact>> findByMember(Member member);

    // 같은 그룹 내부 속성이 여러개 존재할 수 있음
    Optional<List<Contact>> findByGroup(Group group);

    // 다른 그룹 내부에 동일한 이름이 존재할 수 있음
    Optional<List<Contact>> findByName(String name);
    
    Optional<Contact> findByGroupAndName(Group group, String name);

    Optional<Contact> findByGroupAndTelNum(Group group, String telNum);

    // 같은 번호의 유저가 다른 그룹에 속해있을 수 있음
    Optional<List<Contact>> findByTelNum(String telNum);

    // TODO: Group, 전화번호를 통해 Contact 참조
}
