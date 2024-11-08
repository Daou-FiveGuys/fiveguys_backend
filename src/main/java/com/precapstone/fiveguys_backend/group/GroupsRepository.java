package com.precapstone.fiveguys_backend.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Groups, Integer> {
    // 그룹 이름은 단일 개체이다.
    Optional<Groups> findByGroupsName(String groupName);

    Optional<List<Groups>> findByParent(Groups parentGroups);
}
