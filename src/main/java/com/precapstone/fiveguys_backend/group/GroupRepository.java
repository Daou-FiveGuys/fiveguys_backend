package com.precapstone.fiveguys_backend.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    // 그룹 이름은 단일 개체이다.
    Optional<Group> findByGroupName(String groupName);

    // 상위 그룹은 단일 그룹임
    Optional<Group> findByParentGroup(Group parentGroup);
    
    // 하위 그룹에 여러 그룹이 존재할 수 있음
    Optional<List<Group>> findByChildGroup(Group childGroup);
}
